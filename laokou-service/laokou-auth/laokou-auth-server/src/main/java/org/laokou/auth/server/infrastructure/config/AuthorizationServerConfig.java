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
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.laokou.auth.server.infrastructure.convert.Oauth2PasswordAuthenticationConverter;
import org.laokou.auth.server.infrastructure.handler.CustomAuthenticationFailureHandler;
import org.laokou.auth.server.infrastructure.provider.Oauth2PasswordAuthenticationProvider;
import org.laokou.auth.server.infrastructure.token.Oauth2JwtTokenCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Spring Security配置
 * SpringSecurity最新版本更新
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/5/28 0028 上午 10:33
 */
@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    /**
     * https://docs.spring.io/spring-authorization-server/docs/current/reference/html/configuration-model.html
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http
//    , Oauth2PasswordAuthenticationConverter oauth2PasswordAuthenticationConverter
    , Oauth2PasswordAuthenticationProvider oauth2PasswordAuthenticationProvider
    , AuthorizationServerSettings authorizationServerSettings
    ) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());
        http.apply(authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> tokenEndpoint.accessTokenRequestConverter(new Oauth2PasswordAuthenticationConverter())))
                // 客户端认证异常
                .clientAuthentication(configurer -> configurer.errorResponseHandler(new CustomAuthenticationFailureHandler()));
        DefaultSecurityFilterChain chain = http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .apply(authorizationServerConfigurer.authorizationServerSettings(authorizationServerSettings))
                .and()
                .formLogin(Customizer.withDefaults())
                .build();
        http.authenticationProvider(oauth2PasswordAuthenticationProvider);
        return chain;
    }

    @Bean
    RegisteredClientRepository registeredClientRepository() {
        InMemoryRegisteredClientRepository inMemoryRegisteredClientRepository = new InMemoryRegisteredClientRepository(
                RegisteredClient.withId("client_auth")
                        .id("client_auth")
                        .clientId("client_auth")
                        .clientSecret("{noop}secret")
                        .authorizationGrantType(new AuthorizationGrantType(OAuth2ParameterNames.PASSWORD))
                        .clientName("用户认证")
                        .scope("auth")
                        .build());
        return inMemoryRegisteredClientRepository;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public OAuth2TokenGenerator<OAuth2Token> oAuth2TokenGenerator(JwtEncoder jwtEncoder) {
        JwtGenerator generator = new JwtGenerator(jwtEncoder);
        generator.setJwtCustomizer(new Oauth2JwtTokenCustomizer());
        return new DelegatingOAuth2TokenGenerator(generator, new OAuth2RefreshTokenGenerator());
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    private RSAKey generateRsa() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
    }

}
