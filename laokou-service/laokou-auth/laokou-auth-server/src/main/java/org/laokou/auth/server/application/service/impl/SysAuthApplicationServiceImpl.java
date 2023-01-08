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
package org.laokou.auth.server.application.service.impl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.infrastructure.context.CustomAuthorizationServerContext;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.auth.server.infrastructure.server.AuthenticationServer;
import org.laokou.auth.server.infrastructure.server.AuthToken;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.swagger.exception.CustomException;
import org.laokou.common.swagger.exception.ErrorCode;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.utils.*;
import org.laokou.redis.utils.RedisKeyUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
/**
 * SpringSecurity最新版本更新
 * @author laokou
 */
@Service
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SysAuthApplicationServiceImpl implements SysAuthApplicationService {
    private final RegisteredClientRepository registeredClientRepository;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationServerSettings authorizationServerSettings;
    private final LoginLogUtil loginLogUtil;
    private final RedisUtil redisUtil;

    @Override
    public AuthToken login(HttpServletRequest request) {
        // 1.验证认证相关信息
        RegisteredClient registeredClient = loginBefore(request);
        // 2.登录中
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = loginInfo(request);
        // 3.生成token
        AuthToken authToken = loginAfter(registeredClient, usernamePasswordAuthenticationToken, request);
        return authToken;
    }

    private RegisteredClient loginBefore(HttpServletRequest request) {
        // 1.验证clientId
        String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
        if (StringUtil.isEmpty(clientId)) {
            throw new CustomException(ErrorCode.INVALID_CLIENT);
        }
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            throw new CustomException(ErrorCode.INVALID_CLIENT);
        }
        // 2.验证clientSecret
        String secret = request.getParameter(OAuth2ParameterNames.CLIENT_SECRET);
        String clientSecret = registeredClient.getClientSecret();
        if (!passwordEncoder.matches(secret,clientSecret)) {
            throw new CustomException(ErrorCode.INVALID_CLIENT);
        }
        // 3.验证scope
        String scope = request.getParameter(OAuth2ParameterNames.SCOPE);
        Set<String> scopes = registeredClient.getScopes();
        if (StringUtil.isEmpty(scope)) {
            throw new CustomException(ErrorCode.INVALID_SCOPE);
        }
        List<String> scopeList = Arrays.asList(scope.split(Constant.COMMA));
        for (String s : scopeList) {
            if (!scopes.contains(s)) {
                throw new CustomException(ErrorCode.INVALID_SCOPE);
            }
        }
        return registeredClient;
    }

    private UsernamePasswordAuthenticationToken loginInfo(HttpServletRequest request) {
        return authenticationToken(request).login(request);
    }

    private AuthenticationServer authenticationToken(HttpServletRequest request) {
        // 1.验证grantType
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (StringUtil.isEmpty(grantType)) {
            throw new CustomException(ErrorCode.UNSUPPORTED_GRANT_TYPE);
        }
        try {
            String className = AuthenticationServer.class.getSimpleName();
            AuthenticationServer authenticationServer = SpringContextUtil.getBean(grantType + className, AuthenticationServer.class);
            // 2.验证账号/密码/验证码 或 手机号/验证码等等
            return authenticationServer;
        } catch (NoSuchBeanDefinitionException e ) {
            throw new CustomException(ErrorCode.UNSUPPORTED_GRANT_TYPE);
        }
    }

    private AuthToken loginAfter(
            RegisteredClient registeredClient
            , UsernamePasswordAuthenticationToken principal
            , HttpServletRequest request) {
        // 1.生成token（access_token + refresh_token）
        // 获取认证类型
        AuthorizationGrantType grantType = authenticationToken(request).getGrantType();
        // 获取认证范围
        Set<String> scopes = registeredClient.getScopes();
        String loginName = principal.getCredentials().toString();
        // 获取上下文
        DefaultOAuth2TokenContext.Builder builder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(principal)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizedScopes(scopes)
                .authorizationServerContext(new CustomAuthorizationServerContext(request,authorizationServerSettings))
                .authorizationGrantType(grantType);
        DefaultOAuth2TokenContext context = builder.build();
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .principalName(loginName)
                .authorizedScopes(scopes)
                .authorizationGrantType(grantType);
        // 生成access_token
        OAuth2Token generatedOauth2AccessToken = Optional.ofNullable(tokenGenerator.generate(context)).orElseThrow(() -> new CustomException("令牌生成器无法生成访问令牌"));
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER
                , generatedOauth2AccessToken.getTokenValue()
                , generatedOauth2AccessToken.getIssuedAt()
                , generatedOauth2AccessToken.getExpiresAt()
                , context.getAuthorizedScopes());
        // jwt
        if (generatedOauth2AccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(oAuth2AccessToken,
                    meta -> meta.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME
                            ,((ClaimAccessor)generatedOauth2AccessToken).getClaims()))
                    .authorizedScopes(scopes)
                    .attribute(Principal.class.getName(), principal);
        }else {
            authorizationBuilder.accessToken(oAuth2AccessToken);
        }
        // 生成refresh_token
        context = builder
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .tokenType(OAuth2TokenType.REFRESH_TOKEN)
                .build();
        OAuth2Token generateOauth2RefreshToken = Optional.ofNullable(tokenGenerator.generate(context)).orElseThrow(() -> new CustomException("令牌生成器无法生成刷新令牌"));
        authorizationBuilder.refreshToken((OAuth2RefreshToken) generateOauth2RefreshToken);
        OAuth2Authorization oAuth2Authorization = authorizationBuilder.build();
        // 放入内存
        oAuth2AuthorizationService.save(oAuth2Authorization);
        // 2.响应给前端
        String accessToken = generatedOauth2AccessToken.getTokenValue();
        String refreshToken = generateOauth2RefreshToken.getTokenValue();
        Instant expiresAt = generatedOauth2AccessToken.getExpiresAt();
        Instant issuedAt = generatedOauth2AccessToken.getIssuedAt();
        String tokenType = OAuth2AccessToken.TokenType.BEARER.getValue();
        long expireIn = ChronoUnit.SECONDS.between(issuedAt, expiresAt);
        String loginType = grantType.getValue();
        // 登录成功
        loginLogUtil.recordLogin(loginName,loginType, ResultStatusEnum.SUCCESS.ordinal(), AuthConstant.LOGIN_SUCCESS_MSG,request);
        return new AuthToken(accessToken,refreshToken,tokenType,expireIn);
    }

    @Override
    public String captcha(HttpServletRequest request) {
        return authenticationToken(request).captcha(request);
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        String token = request.getHeader(Constant.AUTHORIZATION_HEAD);
        if (StringUtil.isEmpty(token)) {
            return true;
        }
        token = token.substring(7);
        OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (oAuth2Authorization == null) {
            return true;
        }
        UserDetail userDetail = (UserDetail) ((UsernamePasswordAuthenticationToken) oAuth2Authorization.getAttribute(Principal.class.getName())).getPrincipal();
        // 清空用户信息
        oAuth2AuthorizationService.remove(oAuth2Authorization);
        // 清空用户key
        String userInfoKey = RedisKeyUtil.getUserInfoKey(token);
        redisUtil.delete(userInfoKey);
        Long userId = userDetail.getUserId();
        String resourceTreeKey = RedisKeyUtil.getResourceTreeKey(userId);
        redisUtil.delete(resourceTreeKey);
        return true;
    }

}
