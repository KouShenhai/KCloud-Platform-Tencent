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
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.github.benmanes.caffeine.cache.Cache;
import org.laokou.oss.client.vo.SysOssVO;
import java.io.InputStream;

/**
 * @author laokou
 */
public abstract class AbstractStorageService implements StorageService{

    protected SysOssVO vo;
    protected Cache<String,Object> caffeineCache;

    public String upload(int limitRead, long size, String fileName, InputStream inputStream, String contentType) {
        // 获取AmazonS3
        AmazonS3 amazonS3 = getAmazonS3();
        // 创建bucket
        createBucket(amazonS3);
        // 上传文件
        putObject(amazonS3,limitRead,size,fileName,inputStream,contentType);
        // 获取地址
        return getUrl(amazonS3, fileName);
    }

    private AmazonS3 getAmazonS3() {
        String uuid = vo.getId().toString();
        Object obj = caffeineCache.getIfPresent(uuid);
        if (obj != null) {
            return (AmazonS3) obj;
        }
        String accessKey = vo.getAccessKey();
        String secretKey = vo.getSecretKey();
        String region = vo.getRegion();
        String endpoint = vo.getEndpoint();
        Boolean pathStyleAccessEnabled = vo.getPathStyleAccessEnabled();
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(awsCredentialsProvider)
                .withPathStyleAccessEnabled(pathStyleAccessEnabled)
                .build();
        caffeineCache.put(uuid,amazonS3);
        return amazonS3;
    }

}
