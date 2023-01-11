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
package org.laokou.auth.server.infrastructure.authentication;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.laokou.auth.client.exception.CustomAuthExceptionHandler;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.core.utils.HttpContextUtil;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.common.swagger.exception.CustomException;
import org.laokou.common.swagger.exception.ErrorCode;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
/**
 * 邮件/手机/密码
 * @author laokou
 */
public abstract class OAuth2BaseAuthenticationProvider implements AuthenticationProvider {

    protected SysUserServiceImpl sysUserService;
    protected SysMenuService sysMenuService;
    protected SysDeptService sysDeptService;
    protected LoginLogUtil loginLogUtil;
    protected PasswordEncoder passwordEncoder;
    protected SysCaptchaService sysCaptchaService;
    protected OAuth2AuthorizationService authorizationService;
    protected OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    public OAuth2BaseAuthenticationProvider(
            SysUserServiceImpl sysUserService
            , SysMenuService sysMenuService
            , SysDeptService sysDeptService
            , LoginLogUtil loginLogUtil
            , PasswordEncoder passwordEncoder
            , SysCaptchaService sysCaptchaService
            , OAuth2AuthorizationService authorizationService
            , OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        this.sysDeptService = sysDeptService;
        this.sysMenuService = sysMenuService;
        this.loginLogUtil = loginLogUtil;
        this.sysUserService = sysUserService;
        this.passwordEncoder = passwordEncoder;
        this.sysCaptchaService = sysCaptchaService;
        this.tokenGenerator = tokenGenerator;
        this.authorizationService = authorizationService;
    }


    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        Authentication usernamePasswordToken = login(request);
        return getToken(authentication,usernamePasswordToken);
    }

    abstract public boolean supports(Class<?> authentication);

    /**
     * 登录
     * @param request
     * @return
     */
    abstract Authentication login(HttpServletRequest request);

    /**
     * 认证类型
     * @return
     * @return
     */
    abstract AuthorizationGrantType getGrantType();

    /**
     * 仿照授权码模式
     * @param authentication
     * @param principal
     * @return
     */
    protected Authentication getToken(Authentication authentication,Authentication principal) {
        // 生成token（access_token + refresh_token）
        OAuth2BaseAuthenticationToken OAuth2BaseAuthenticationToken = (OAuth2BaseAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(OAuth2BaseAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        // 获取认证范围
        Set<String> scopes = registeredClient.getScopes();
        String loginName = principal.getCredentials().toString();
        // 认证类型
        AuthorizationGrantType grantType = getGrantType();
        // 获取上下文
        DefaultOAuth2TokenContext.Builder builder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(principal)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizedScopes(scopes)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
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
                    // admin后台管理需要token，解析token获取用户信息，因此将用户信息存在数据库，下次直接查询数据库就可以获取用户信息
                    .attribute(Principal.class.getName(), principal);
        }else {
            authorizationBuilder.accessToken(oAuth2AccessToken);
        }
        // 生成refresh_token
        context = builder
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .tokenType(OAuth2TokenType.REFRESH_TOKEN)
                .build();
        OAuth2Token generateOauth2RefreshToken = Optional.ofNullable(tokenGenerator.generate(context)).orElseThrow(() ->
                new CustomException("令牌生成器无法生成刷新令牌"));
        OAuth2RefreshToken oAuth2RefreshToken = (OAuth2RefreshToken) generateOauth2RefreshToken;
        authorizationBuilder.refreshToken(oAuth2RefreshToken);
        OAuth2Authorization oAuth2Authorization = authorizationBuilder.build();
        authorizationService.save(oAuth2Authorization);
        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient, clientPrincipal, oAuth2AccessToken, oAuth2RefreshToken, Collections.emptyMap());
    }
    protected UsernamePasswordAuthenticationToken getUserInfo(String loginName, String password, HttpServletRequest request) {
        AuthorizationGrantType grantType = getGrantType();
        String loginType = grantType.getValue();
        // 验证用户
        UserDetail userDetail = sysUserService.getUserDetail(loginName);
        if (userDetail == null) {
            loginLogUtil.recordLogin(loginName,loginType, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR),request);
            CustomAuthExceptionHandler.throwError(ErrorCode.ACCOUNT_PASSWORD_ERROR, MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR));
        }
        if (OAuth2PasswordAuthenticationProvider.GRANT_TYPE.equals(grantType)) {
            // 验证密码
            String clientPassword = userDetail.getPassword();
            if (!passwordEncoder.matches(password, clientPassword)) {
                loginLogUtil.recordLogin(loginName, loginType, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR), request);
                CustomAuthExceptionHandler.throwError(ErrorCode.ACCOUNT_PASSWORD_ERROR, MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR));
            }
        }
        // 是否锁定
        if (!userDetail.isEnabled()) {
            loginLogUtil.recordLogin(loginName,loginType, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_DISABLE),request);
            CustomAuthExceptionHandler.throwError(ErrorCode.ACCOUNT_DISABLE, MessageUtil.getMessage(ErrorCode.ACCOUNT_DISABLE));
        }
        Long userId = userDetail.getUserId();
        Integer superAdmin = userDetail.getSuperAdmin();
        // 权限标识列表
        List<String> permissionsList = sysMenuService.getPermissionsList(superAdmin,userId);
        if (CollectionUtils.isEmpty(permissionsList)) {
            loginLogUtil.recordLogin(loginName,loginType, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS),request);
            CustomAuthExceptionHandler.throwError(ErrorCode.NOT_PERMISSIONS, MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS));
        }
        // 部门列表
        List<Long> deptIds = sysDeptService.getDeptIds(superAdmin,userId);
        userDetail.setDeptIds(deptIds);
        userDetail.setPermissionList(permissionsList);
        return new UsernamePasswordAuthenticationToken(userDetail,loginName,userDetail.getAuthorities());
    }

    private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

}
