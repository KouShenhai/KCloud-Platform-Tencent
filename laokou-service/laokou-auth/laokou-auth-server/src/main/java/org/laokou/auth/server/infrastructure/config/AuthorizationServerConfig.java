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
package org.laokou.auth.server.infrastructure.config;
import lombok.AllArgsConstructor;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.server.infrastructure.exception.CustomAuthenticationEntryPoint;
import org.laokou.auth.server.infrastructure.exception.CustomClientCredentialsTokenEndpointFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

/**
 * @author Kou Shenhai
 * 官方不再维护，过期类无法替换
 * @version 1.0
 */
@Configuration
@AllArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final AuthenticationManager authenticationManager;
    private final WebResponseExceptionTranslator<OAuth2Exception> webResponseExceptionTranslator;
    private final TokenStore tokenStore;
    private final ClientDetailsService clientDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final DataSource dataSource;
    /**
     * 配置客户端信息
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setSelectClientDetailsSql(AuthConstant.SELECT_STATEMENT);
        clientDetailsService.setFindClientDetailsSql(AuthConstant.FIND_STATEMENT);
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS);
        //密码模式
        endpoints.authenticationManager(authenticationManager);
        //登录或者鉴权失败时的返回信息(自定义)
        endpoints.exceptionTranslator(webResponseExceptionTranslator);
        // 令牌配置
        endpoints.tokenServices(tokenServices());
    }


    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices services = new DefaultTokenServices();
        // 客户端详情服务
        services.setClientDetailsService(clientDetailsService);
        // 支持令牌刷新
        services.setSupportRefreshToken(true);
        // 存储令牌策略
        services.setTokenStore(tokenStore);
        return services;
    }

    /**
     * 详情参考下面给的两个方法
     * {@link AuthorizationServerSecurityConfigurer#configure(HttpSecurity)}
     * {@link AuthorizationServerSecurityConfigurer#clientCredentialsTokenEndpointFilter(HttpSecurity)}
     * @param security a fluent configurer for security features
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // 不开启allowFormAuthenticationForClients，自定义ClientCredentialsTokenEndpointFilter
        CustomClientCredentialsTokenEndpointFilter endpointFilter = new CustomClientCredentialsTokenEndpointFilter(security);
        endpointFilter.setAuthenticationEntryPoint(customAuthenticationEntryPoint);
        /**
         * 开启allowFormAuthenticationForClients，ClientCredentialsTokenEndpointFilter会创建对象并赋值，通过调用postProcess(clientCredentialsTokenEndpointFilter)（对象后置处理），将new的对象放到spring容器进行管理，因此会调用afterPropertiesSet
         * 调用endpointFilter.afterPropertiesSet()，将已经进行属性填充的对象进行初始化，只需要实现InitializingBean即可（InitializingBean只有一个方法，那就是afterPropertiesSet）
         */
        endpointFilter.afterPropertiesSet();
        security
                // allowFormAuthenticationForClients => 允许表单认证，并且client_id和client_secret会走ClientCredentialsTokenEndpointFilter逻辑（详情查看源码）
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .addTokenEndpointAuthenticationFilter(endpointFilter);
    }

}
