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
package org.laokou.auth.server.application.service.impl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.server.infrastructure.token.AuthenticationToken;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.utils.*;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/**
 * SpringSecurity最新版本更新
 * @author Kou Shenhai
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysAuthApplicationServiceImpl implements SysAuthApplicationService {
    private final RegisteredClientRepository registeredClientRepository;

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) {
        // 1.验证认证相关信息
        RegisteredClient registeredClient = loginBefore(request);
        // 2.登录
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = login(request);
        // 3.生成token
        loginAfter(registeredClient,usernamePasswordAuthenticationToken);
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
        if (!Objects.equals(clientSecret,secret)) {
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

    private UsernamePasswordAuthenticationToken login(HttpServletRequest request) {
        // 1.验证grantType
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (StringUtil.isEmpty(grantType)) {
            throw new CustomException(ErrorCode.UNSUPPORTED_GRANT_TYPE);
        }
        try {
            String className = AuthenticationToken.class.getSimpleName();
            AuthenticationToken authenticationToken = SpringContextUtil.getBean(grantType + className, AuthenticationToken.class);
            // 2.验证账号/密码/验证码 或 手机号/验证码等等
            return authenticationToken.login(request);
        } catch (Exception e) {
            if (e instanceof NoSuchBeanDefinitionException) {
                throw new CustomException(ErrorCode.UNSUPPORTED_GRANT_TYPE);
            } else {
                throw new CustomException(ErrorCode.SERVICE_MAINTENANCE);
            }
        }
    }

    private void loginAfter(RegisteredClient registeredClient
            , UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        // 1.生成token（access_token + refresh_token）

        // 2.响应给前端
    }

    @Override
    public String captcha(HttpServletRequest request) {
        // 1.验证grantType
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (StringUtil.isEmpty(grantType)) {
            throw new CustomException(ErrorCode.UNSUPPORTED_GRANT_TYPE);
        }
        try {
            String className = AuthenticationToken.class.getSimpleName();
            AuthenticationToken authenticationToken = SpringContextUtil.getBean(grantType + className, AuthenticationToken.class);
            // 2.获取验证码
            return authenticationToken.captcha(request);
        } catch (Exception e) {
            if (e instanceof NoSuchBeanDefinitionException) {
                throw new CustomException(ErrorCode.UNSUPPORTED_GRANT_TYPE);
            } else {
                throw new CustomException(ErrorCode.SERVICE_MAINTENANCE);
            }
        }
    }

}
