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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.utils.FileUtil;
import org.laokou.common.core.utils.HashUtil;
import org.laokou.common.core.utils.SpringContextUtil;
import org.laokou.oss.client.vo.CloudStorageVO;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;
/**
 * 本地上传
 * @author : Kou Shenhai
 * @date : 2020-06-21 23:42
 */
@Slf4j
public class LocalStorageService extends AbstractStorageService {

    private static final String[] NODES;

    private static final String RW;

    private static final ThreadPoolTaskExecutor OSS_THREAD_POOL_TASK_EXECUTOR;

    static {
        NODES = new String[]{"node1", "node2", "node3", "node4", "node5"};
        RW = "rw";
        OSS_THREAD_POOL_TASK_EXECUTOR =  (ThreadPoolTaskExecutor) SpringContextUtil.getBean("ossThreadPoolTaskExecutor");
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
           nioRandomFileChannelUpload(cloudStorageVO.getLocalPath(), directoryPath, fileName, inputStream, fileSize, CHUNK_SIZE);
       }
       return cloudStorageVO.getLocalDomain() + directoryPath + SEPARATOR + fileName ;
    }

    /**
     * nio上传文件
     * @param rootPath 根目录
     * @param directoryPath 文件相对目录
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param chunkSize 文件分片
     */
    @SneakyThrows
    private void nioRandomFileChannelUpload(final String rootPath, final String directoryPath, final String fileName, final InputStream inputStream, final Long fileSize, final Long chunkSize) {
        //读通道
        try (FileChannel inChannel = ((FileInputStream)inputStream).getChannel()) {
            log.info("文件传输开始...");
            //新建目录
            final File newFile = FileUtil.uploadBefore(rootPath,directoryPath,fileName);
            //需要分多少个片
            final long chunkCount = (fileSize / chunkSize) + (fileSize % chunkSize == 0 ? 0 : 1);
            //同步工具，允许1或N个线程等待其他线程完成执行
            final CountDownLatch latch = new CountDownLatch((int) chunkCount);
            //position 指针 > 读取或写入的位置
            for (long index = 0, position = 0, finalEndSize = position + chunkSize ; index < chunkCount; index++,position = index * chunkSize) {
                //指定位置
                final Long finalPosition = position;
                //读通道
                OSS_THREAD_POOL_TASK_EXECUTOR.execute(new RandomFileChannelRun(finalPosition,finalEndSize, fileSize, newFile, inChannel,latch));
            }
            // 等待其他线程
            latch.await();
            log.info("文件传输结束...");
        }
    }

    private class RandomFileChannelRun extends Thread {
        // 写通道
        private final File newFile;
        // 读通道
        private final FileChannel srcChannel;
        // 读取或写入的位置
        private final long position;
        // 结束位置
        private long endSize;
        // 计数器
        private final CountDownLatch latch;
        // 文件大小
        private final Long fileSize;

        RandomFileChannelRun(final Long position,final Long endSize,final Long fileSize,final File newFile, final FileChannel srcChannel,final CountDownLatch latch) {
            this.newFile = newFile;
            this.srcChannel = srcChannel;
            this.fileSize = fileSize;
            this.latch = latch;
            this.endSize = endSize;
            this.position = position;
        }

        @SneakyThrows
        @Override
        public void run() {
            //结束位置
            if (endSize > fileSize) {
                endSize = fileSize;
            }
            try (
                    // 随机文件读取
                    RandomAccessFile accessFile = new RandomAccessFile(newFile, RW);
                    // 写通道
                    FileChannel newChannel = accessFile.getChannel()
            ) {
                // 标记位置
                newChannel.position(position);
                // 零拷贝
                // transferFrom 与 transferTo 区别
                // transferTo 最多拷贝2gb，和源文件大小保持一致
                // transferFrom 每个线程拷贝20MB
                srcChannel.transferTo(position, endSize, newChannel);
            } finally {
                // 减一，当为0时，线程就会执行
                latch.countDown();
            }
        }
    }

}
