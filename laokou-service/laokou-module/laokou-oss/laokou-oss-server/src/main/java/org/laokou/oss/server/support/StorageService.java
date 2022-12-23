package org.laokou.oss.server.support;
import java.io.InputStream;
/**
 * @author laokou
 */
public interface StorageService {

    /**
     * 按1M分片
     */
    Long CHUNK_SIZE = 1L * 1024 * 1024;

    String SEPARATOR = "/";

    String ROD = "-";

    /**
     * 上传文件
     * @param inputStream 文件流
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @return
     * @throws Exception
     */
    String upload(InputStream inputStream, String fileName, Long fileSize) throws Exception;

}
