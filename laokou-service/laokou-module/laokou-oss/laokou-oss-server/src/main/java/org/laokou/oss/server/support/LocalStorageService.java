/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
import org.laokou.common.core.utils.FileUtil;
import org.laokou.common.core.utils.HashUtil;
import org.laokou.common.core.utils.SpringContextUtil;
import org.laokou.oss.client.vo.CloudStorageVO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 本地上传
 * @author : Kou Shenhai
 * @date : 2020-06-21 23:42
 */
public class LocalStorageService extends AbstractStorageService {

    private static final String[] NODES;

    private static final ThreadPoolExecutor OSS_THREAD_POOL;

    static {
        NODES = new String[]{"node1", "node2", "node3", "node4", "node5"};
        OSS_THREAD_POOL =  (ThreadPoolExecutor) SpringContextUtil.getBean("ossThreadPool");
    }

    public LocalStorageService(CloudStorageVO vo){
        this.cloudStorageVO = vo;
    }

    @Override
    public String upload(InputStream inputStream,String fileName,Long fileSize) throws IOException {
       fileName = IdUtil.simpleUUID() + FileUtil.getFileSuffix(fileName);
       String directoryPath = SEPARATOR + cloudStorageVO.getLocalPrefix() + SEPARATOR + NODES[HashUtil.getHash(fileName) & (NODES.length - 1)];
       //上传文件
       if (inputStream instanceof ByteArrayInputStream) {
           FileUtil.fileUpload(cloudStorageVO.getLocalPath(), directoryPath, fileName, inputStream);
       } else {
           FileUtil.nioRandomFileChannelUpload(cloudStorageVO.getLocalPath(), directoryPath, fileName, inputStream, fileSize, CHUNK_SIZE,OSS_THREAD_POOL);
       }
       return cloudStorageVO.getLocalDomain() + directoryPath + SEPARATOR + fileName ;
    }

}
