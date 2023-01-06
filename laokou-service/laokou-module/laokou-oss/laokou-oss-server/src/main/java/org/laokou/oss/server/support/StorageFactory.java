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
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.laokou.common.swagger.exception.CustomException;
import org.laokou.oss.client.vo.SysOssVO;
import org.laokou.redis.utils.RedisKeyUtil;
import org.laokou.oss.server.service.SysOssService;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.stereotype.Component;
/**
 * @author laokou
 */
@Component
@RequiredArgsConstructor
public class StorageFactory {

    private final SysOssService sysOssService;

    private final RedisUtil redisUtil;

    private final Cache<String,Object> caffeineCache;

   public StorageService build(){
       String ossConfigKey = RedisKeyUtil.getOssConfigKey();
       Object vo = caffeineCache.getIfPresent(ossConfigKey);
       if (vo == null) {
           Object object = redisUtil.get(ossConfigKey);
           if (object == null) {
               vo = sysOssService.queryOssConfig();
               if (null == vo) {
                   throw new CustomException("请配置OSS");
               }
               redisUtil.set(ossConfigKey, vo, RedisUtil.HOUR_ONE_EXPIRE);
           } else {
               vo = object;
               caffeineCache.put(ossConfigKey,vo);
           }
       }
       return new AmazonS3StorageService((SysOssVO) vo,caffeineCache);
   }

}
