/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.admin.server.infrastructure.component.cloud;
import org.laokou.admin.server.infrastructure.component.handler.impl.AdminHandler;
import org.laokou.admin.server.infrastructure.config.CloudStorageConfig;
import org.laokou.admin.client.enums.CloudTypeEnum;
import org.laokou.common.exception.CustomException;
import org.laokou.common.utils.JacksonUtil;
import org.laokou.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @author Kou Shenhai
 */
@Component
public class CloudFactory {

    @Autowired
    private AdminHandler adminHandler;

   public AbstractCloudStorageService build(){
       String oss = adminHandler.getOss();
       if (StringUtil.isBlank(oss)){
           throw new CustomException("请配置OSS");
       }
       CloudStorageConfig config = JacksonUtil.toBean(oss, CloudStorageConfig.class);
       if (CloudTypeEnum.ALIYUN.ordinal() == config.getType()){
           return new AliyunCloudStorageService(config);
       }
       if (CloudTypeEnum.LOCAL.ordinal() == config.getType()){
           return new LocalCloudStorageService(config);
       }
       if (CloudTypeEnum.FASTDFS.ordinal() == config.getType()){
           return new FastDFSCloudStorageService(config);
       }
       throw new CustomException("请检查OSS相关配置");
   }

}
