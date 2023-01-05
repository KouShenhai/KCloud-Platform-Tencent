package org.laokou.oss.server.support;

import com.amazonaws.services.s3.AmazonS3;
import java.io.InputStream;

/**
 * @author laokou
 */
public interface StorageService {

    /**
     * 创建bucket
     * @param amazonS3
     */
    void createBucket(AmazonS3 amazonS3);

    /**
     * 上传文件
     * @param amazonS3
     * @param readLimit
     * @param size
     * @param fileName
     * @param inputStream
     * @param contentType
     * @param md5
     */
    void putObject(AmazonS3 amazonS3,String md5, int readLimit, long size, String fileName, InputStream inputStream,String contentType);

    /**
     * 获取地址
     * @param amazonS3
     * @param fileName
     * @return
     */
    String getUrl(AmazonS3 amazonS3,String fileName);

    /**
     * 上传文件
     * @param fileName
     * @param inputStream
     * @param contentType
     * @param size
     * @param readLimit
     * @param md5
     * @return
     */
    String upload(int readLimit,String md5, long size, String fileName, InputStream inputStream,String contentType);
}
