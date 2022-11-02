package org.laokou.oss.server.cloud;

import cn.hutool.core.util.IdUtil;
import io.minio.*;
import org.laokou.common.utils.FileUtil;
import org.laokou.oss.client.vo.CloudStorageVO;
import java.io.InputStream;

/**
 * @author Kou Shenhai
 */
public class MinioCloudStorageService extends AbstractCloudStorageService{

    private MinioClient minioClient;

    private void init() {
        minioClient = MinioClient.builder()
                .endpoint(cloudStorageVO.getMinioEndPoint())
                .credentials(cloudStorageVO.getMinioAccessKey(),cloudStorageVO.getMinioSecretKey())
                .build();
    }

    public MinioCloudStorageService(CloudStorageVO vo) {
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
        PutObjectArgs putObjectArgs = PutObjectArgs.builder().stream(inputStream, fileSize, -1)
                .bucket(cloudStorageVO.getMinioBucketName())
                .object(fileName)
                .build();
        minioClient.putObject(putObjectArgs);
        return cloudStorageVO.getMinioEndPoint() + "/" + cloudStorageVO.getMinioBucketName() + "/" + fileName;
    }
}
