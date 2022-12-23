/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.oss.server.support;

import cn.hutool.core.util.IdUtil;
import io.minio.*;
import org.laokou.common.core.utils.FileUtil;
import org.laokou.common.core.utils.HashUtil;
import org.laokou.oss.client.vo.CloudStorageVO;
import java.io.InputStream;

/**
 * @author laokou
 */
public class MinioStorageService extends AbstractStorageService {

    private MinioClient minioClient;

    private static final String[] NODES = {"node1","node2","node3","node4","node5"};

    private void init() {
        minioClient = MinioClient.builder()
                .endpoint(cloudStorageVO.getMinioEndPoint())
                .credentials(cloudStorageVO.getMinioAccessKey(),cloudStorageVO.getMinioSecretKey())
                .build();
    }

    public MinioStorageService(CloudStorageVO vo) {
        this.cloudStorageVO = vo;
        init();
    }

    @Override
    public String upload(InputStream inputStream, String fileName, Long fileSize) throws Exception {
        // 如果BucketName不存在，则创建
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(cloudStorageVO.getMinioBucketName()).build();
        boolean bucketExists = minioClient.bucketExists(bucketExistsArgs);
        if (!bucketExists) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(cloudStorageVO.getMinioBucketName()).build();
            minioClient.makeBucket(makeBucketArgs);
        }
        fileName = IdUtil.simpleUUID() + FileUtil.getFileSuffix(fileName);
        String directoryPath = SEPARATOR + NODES[HashUtil.getHash(fileName) & (NODES.length - 1)];
        PutObjectArgs putObjectArgs = PutObjectArgs.builder().stream(inputStream, fileSize, -1)
                .bucket(cloudStorageVO.getMinioBucketName())
                .object(directoryPath + SEPARATOR + fileName)
                .build();
        minioClient.putObject(putObjectArgs);
        return cloudStorageVO.getMinioEndPoint() + SEPARATOR + cloudStorageVO.getMinioBucketName() + directoryPath + SEPARATOR + fileName;
    }
}
