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
import org.springframework.context.annotation.Configuration;
/**
 * SpringSecurity最新版本更新
 * @author laokou
 * @version 1.0
 * @date 2021/5/30 0030 下午 2:48
 */
@Configuration
public class ResourceServerConfig {

//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http
//                .httpBasic().disable()
//                .cors().disable()
//                .csrf().disable()
//                .formLogin().disable()
//                .authorizeRequests()
//                .antMatchers("/druid/**"
//                        ,"/webjars/**"
//                        ,"/swagger-resources/**"
//                        ,"/doc.html"
//                        ,"/v2/api-docs"
//                        ,"/swagger/api-docs"
//                        ,"/actuator/**"
//                        ,"/ws/**").permitAll()
//                .anyRequest().authenticated();
//    }
}
