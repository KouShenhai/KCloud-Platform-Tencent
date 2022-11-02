package org.laokou.oss.server.cloud;

import io.minio.*;
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
        String filePath = cloudStorageVO.getMinioPrefix() + "/" + fileName;
        PutObjectArgs putObjectArgs = PutObjectArgs.builder().stream(inputStream, -1, chunkSize)
                .bucket(cloudStorageVO.getMinioBucketName())
                .object(filePath)
                .build();
        minioClient.putObject(putObjectArgs);
        return cloudStorageVO.getMinioEndPoint() + "/" + cloudStorageVO.getMinioBucketName() + filePath;
    }
}
