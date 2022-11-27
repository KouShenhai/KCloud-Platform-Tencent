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
package org.laokou.auth.server.infrastructure.token;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
/**
 * TokenStore
 * 官方不再维护，过期类无法替换
 * @author Kou Shenhai
 */
@Configuration
@RequiredArgsConstructor
public class CustomTokenStore {

    private final LettuceConnectionFactory lettuceConnectionFactory;

    @Bean
    public TokenStore tokenStore() {
        // 与业务库区分
        lettuceConnectionFactory.setDatabase(1);
        RedisTokenStore redisTokenStore = new RedisTokenStore(lettuceConnectionFactory);
        return redisTokenStore;
    }

}
