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
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.auth.server.infrastructure.token.EmailAuthenticationToken;
import org.laokou.auth.server.infrastructure.token.PasswordAuthenticationToken;
import org.laokou.auth.server.infrastructure.token.SmsAuthenticationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import java.time.Duration;
import java.util.List;
/**
 * Spring Security配置
 * SpringSecurity最新版本更新
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/5/28 0028 上午 10:33
 */
@Configuration
public class AuthorizationServerConfig {

    /**
     * https://docs.spring.io/spring-authorization-server/docs/current/reference/html/configuration-model.html
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.build();
    }

    @Bean
    RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        InMemoryRegisteredClientRepository inMemoryRegisteredClientRepository = new InMemoryRegisteredClientRepository(
                RegisteredClient.withId("client_auth")
                        .clientId("client_auth")
                        .clientSecret(passwordEncoder.encode("secret"))
                        // ClientAuthenticationMethod.CLIENT_SECRET_BASIC => client_id:client_secret 进行Base64编码后的值
                        // Headers Authorization Basic Y2xpZW50X2F1dGg6c2VjcmV0
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantTypes(authorizationGrantTypes -> authorizationGrantTypes.addAll(
                                List.of(AuthorizationGrantType.AUTHORIZATION_CODE
                                        , AuthorizationGrantType.REFRESH_TOKEN
                                        , new AuthorizationGrantType(PasswordAuthenticationToken.GRANT_TYPE)
                                        , new AuthorizationGrantType(SmsAuthenticationToken.GRANT_TYPE)
                                        , new AuthorizationGrantType(EmailAuthenticationToken.GRANT_TYPE)
                                        , AuthorizationGrantType.CLIENT_CREDENTIALS)))
                        // 支持OIDC
                        .scopes(scopes -> scopes.addAll(List.of(
                                "auth", OidcScopes.OPENID
                        )))
                        .redirectUris(redirectUris -> redirectUris.addAll(List.of(
                                "https://spring.io"
                        )))
                        .clientName("认证")
                        // JWT配置
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                                .accessTokenTimeToLive(Duration.ofHours(1))
                                .refreshTokenTimeToLive(Duration.ofHours(6))
                                .build())
                        // 客户端相关配置，包括验证密钥或需要授权页面
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .build());
        return inMemoryRegisteredClientRepository;
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    OAuth2AuthorizationService auth2AuthorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    UserDetailsService userDetailsService(
            SysUserServiceImpl sysUserService
            , SysMenuService sysMenuService
            , SysDeptService sysDeptService
            , SysCaptchaService sysCaptchaService
            , LoginLogUtil loginLogUtil
            , PasswordEncoder passwordEncoder) {
        return new PasswordAuthenticationToken(sysUserService,sysMenuService
                , sysDeptService
                , sysCaptchaService
                , loginLogUtil
                , passwordEncoder);
    }
}
