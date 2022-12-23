///**
// * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// *   http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.laokou.admin.server.infrastructure.config;
//
//import lombok.AllArgsConstructor;
//import org.laokou.auth.client.exception.AuthExceptionHandler;
//import org.laokou.auth.client.exception.SecurityAuthenticationEntryPoint;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//
///**
// * SpringSecurity最新版本更新
// * @author laokou
// * @version 1.0
// * @date 2021/5/30 0030 下午 2:48
// */
//@Configuration
//@AllArgsConstructor
//@EnableResourceServer
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
//
//    private TokenStore tokenStore;
//
//    @Override
//    public void configure(ResourceServerSecurityConfigurer resources) {
//        // 无状态
//        resources.stateless(true).tokenStore(tokenStore);
//        //token不存在或错误时，异常处理
//        resources.authenticationEntryPoint(new SecurityAuthenticationEntryPoint())
//                .accessDeniedHandler(new AuthExceptionHandler());
//    }
//
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
//}
