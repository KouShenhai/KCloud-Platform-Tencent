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
package org.laokou.auth.server.infrastructure.config;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.authentication.OAuth2PasswordAuthenticationConverter;
import org.laokou.auth.server.infrastructure.customizer.CustomTokenCustomizer;
import org.laokou.auth.server.infrastructure.handler.CustomAuthenticationFailureHandler;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.auth.server.infrastructure.server.EmailAuthenticationServer;
import org.laokou.auth.server.infrastructure.server.PasswordAuthenticationServer;
import org.laokou.auth.server.infrastructure.server.SmsAuthenticationServer;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
/**
 * Spring Security配置
 * SpringSecurity最新版本更新
 * @author laokou
 * @version 1.0
 * @date 2021/5/28 0028 上午 10:33
 */
@Configuration
public class AuthorizationServerConfig {

    /**
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http
    , AuthorizationServerSettings authorizationServerSettings
    , OAuth2AuthorizationService authorizationService) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        authorizationServerConfigurer.oidc(Customizer.withDefaults());
        http.exceptionHandling(configurer -> configurer.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                .apply(authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> tokenEndpoint.accessTokenRequestConverter(new DelegatingAuthenticationConverter(
                List.of(
                        new OAuth2AuthorizationCodeAuthenticationConverter()
                        , new OAuth2ClientCredentialsAuthenticationConverter()
                        , new OAuth2RefreshTokenAuthenticationConverter()
                        , new OAuth2AuthorizationCodeRequestAuthenticationConverter()
                        , new OAuth2PasswordAuthenticationConverter()))))
                .clientAuthentication(clientAuthentication -> clientAuthentication.errorResponseHandler(new CustomAuthenticationFailureHandler()))
        );
        return http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .authorizeHttpRequests(authorizeRequests -> {
                    // 忽略error
                    authorizeRequests.requestMatchers("/error").permitAll();
                    authorizeRequests.anyRequest().authenticated();
                })
                .csrf(csrf -> csrf.ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher()))
                .apply(authorizationServerConfigurer
                        .authorizationService(authorizationService)
                        .authorizationServerSettings(authorizationServerSettings))
                .and()
                .apply(new FormConfig())
                .and()
                .build();
    }

    @Bean
    RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder,JdbcTemplate jdbcTemplate) {
        RegisteredClient registeredClient = RegisteredClient.withId("auth-client")
                .clientId("auth-client")
                .clientSecret(passwordEncoder.encode("secret"))
                // ClientAuthenticationMethod.CLIENT_SECRET_BASIC => client_id:client_secret 进行Base64编码后的值
                // Headers Authorization Basic YXV0aC1jbGllbnQ6c2VjcmV0
                // http://localhost:1111/oauth2/authorize?client_id=auth-client&client_secret=secret&response_type=code&scope=auth email phone&redirect_uri=https://www.baidu.com
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantTypes(authorizationGrantTypes -> authorizationGrantTypes.addAll(
                        List.of(AuthorizationGrantType.AUTHORIZATION_CODE
                                , AuthorizationGrantType.REFRESH_TOKEN
                                , new AuthorizationGrantType(PasswordAuthenticationServer.GRANT_TYPE)
                                , new AuthorizationGrantType(SmsAuthenticationServer.GRANT_TYPE)
                                , new AuthorizationGrantType(EmailAuthenticationServer.GRANT_TYPE)
                                , AuthorizationGrantType.CLIENT_CREDENTIALS)))
                // 支持OIDC
                .scopes(scopes -> scopes.addAll(List.of(
                          "auth"
                        , OidcScopes.OPENID
                        , OidcScopes.PROFILE
                        , OidcScopes.PHONE
                        , OidcScopes.ADDRESS
                        , OidcScopes.EMAIL
                )))
                .redirectUris(redirectUris -> redirectUris.addAll(List.of(
                          "https://spring.io"
                        , "https://gitee.com/laokouyun"
                        , "https://www.baidu.com"
                )))
                .clientName("认证")
                // JWT配置
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .refreshTokenTimeToLive(Duration.ofHours(6))
                        .build())
                // 客户端相关配置，包括验证密钥或需要授权页面
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
        // Save registered client in db as if in-memory
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        registeredClientRepository.save(registeredClient);
        return registeredClientRepository;
    }

    @Bean
    public OAuth2TokenGenerator<OAuth2Token> oAuth2TokenGenerator(JwtEncoder jwtEncoder) {
        JwtGenerator generator = new JwtGenerator(jwtEncoder);
        generator.setJwtCustomizer(new CustomTokenCustomizer());
        return new DelegatingOAuth2TokenGenerator(generator, new OAuth2RefreshTokenGenerator());
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
    OAuth2AuthorizationService auth2AuthorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    UserDetailsService userDetailsService(
            SysUserServiceImpl sysUserService
            , SysMenuService sysMenuService
            , SysDeptService sysDeptService
            , SysCaptchaService sysCaptchaService
            , LoginLogUtil loginLogUtil
            , PasswordEncoder passwordEncoder
            , RedisUtil redisUtil) {
        return new PasswordAuthenticationServer(sysUserService,sysMenuService
                , sysDeptService
                , sysCaptchaService
                , loginLogUtil
                , passwordEncoder
                , redisUtil);
    }

    @Bean
    AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder
            , UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    /**
     * 加载jwk资源
     * 生成令牌
     * @return
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, JOSEException {
        String alias = "auth";
        String password = "koushenhai";
        String path = "auth.jks";
        ClassPathResource resource = new ClassPathResource(path);
        KeyStore jks = KeyStore.getInstance("jks");
        char[] pwd = password.toCharArray();
        jks.load(resource.getInputStream(),pwd);
        RSAKey rsaKey = RSAKey.load(jks, alias, pwd);
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * JWT解码器
     * 客户端认证授权后，需要访问用户信息，解码器可以从令牌中解析用户信息
     * @return
     * @throws CertificateException
     */
    @Bean
    JwtDecoder jwtDecoder() throws CertificateException, IOException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("x.509");
        // 读取cer公钥证书来配置解码器
        ClassPathResource resource = new ClassPathResource("auth.cer");
        Certificate certificate = certificateFactory.generateCertificate(resource.getInputStream());
        RSAPublicKey publicKey = (RSAPublicKey) certificate.getPublicKey();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

}
