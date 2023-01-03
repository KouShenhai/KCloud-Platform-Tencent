/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.laokou.admin.server.infrastructure.config;

import org.laokou.admin.client.constant.CacheConstant;
import org.laokou.redis.utils.RedisKeyUtil;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author laokou
 */
@Component
public class RedisKeyExpirationEventMessageListener extends KeyExpirationEventMessageListener {

    private CacheManager caffeineCacheManager;

    public RedisKeyExpirationEventMessageListener(RedisMessageListenerContainer listenerContainer,CacheManager caffeineCacheManager) {
        super(listenerContainer);
        this.caffeineCacheManager = caffeineCacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        String regex = ".*";
        if (key.matches(RedisKeyUtil.getUserInfoKey("") + regex)) {
            caffeineCacheManager.getCache(CacheConstant.TOKEN).evict(key);
        }
        if (key.matches(RedisKeyUtil.getDoubleCacheKey(CacheConstant.USER,null) + regex)) {
            caffeineCacheManager.getCache(CacheConstant.USER).evict(key);
        }
    }

    public static void main(String[] args) {
        boolean matches = "sys:user:info:11111".matches(RedisKeyUtil.getUserInfoKey("") + ".*");
        System.out.println(matches);
        boolean matches2 = "sys:user:cache:11111".matches(RedisKeyUtil.getDoubleCacheKey(CacheConstant.USER,null)+ ".*");
        System.out.println(matches2);
    }

}
