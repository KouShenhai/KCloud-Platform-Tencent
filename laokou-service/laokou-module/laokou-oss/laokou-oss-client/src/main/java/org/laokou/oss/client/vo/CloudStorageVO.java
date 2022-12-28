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
package org.laokou.oss.client.vo;
import lombok.Data;
/**
 * 云存储配置信息
 * @author laokou
 */
@Data
public class CloudStorageVO {

    /**
     * 0：本地上传
     * 1：FastDFS
     * 2：阿里云
     * 3：minio
     */
    private Integer type;

    /**
     * 本地上传
     */
    private String localDomain;
    private String localPath;
    private String localPrefix;
    /**
     * FastDFS
     */
    private String fastdfsDomain;
    private String fastdfsGroup;
    /**
     * 阿里云
     */
    private String aliyunDomain;
    private String aliyunPrefix;
    private String aliyunEndPoint;
    private String aliyunAccessKeyId;
    private String aliyunAccessKeySecret;
    private String aliyunBucketName;
    /**
     * minio
     */
    private String minioEndPoint;
    private String minioAccessKey;
    private String minioSecretKey;
    private String minioBucketName;

}
