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
package org.laokou.admin.server.infrastructure.config;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.exception.ForbiddenExceptionHandler;
import org.laokou.auth.client.exception.InvalidAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SpringSecurity最新版本更新
 * @author laokou
 * @version 1.0
 * @date 2021/5/30 0030 下午 2:48
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class ResourceServerConfig {

    private final ForbiddenExceptionHandler forbiddenExceptionHandler;
    private final InvalidAuthenticationEntryPoint invalidAuthenticationEntryPoint;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain resourceFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests().requestMatchers(
                 "/actuator/**"
                        , "/ws/**").permitAll()
                .and()
                .oauth2ResourceServer(oauth2 -> oauth2.accessDeniedHandler(forbiddenExceptionHandler))
                .build();
    }
}
