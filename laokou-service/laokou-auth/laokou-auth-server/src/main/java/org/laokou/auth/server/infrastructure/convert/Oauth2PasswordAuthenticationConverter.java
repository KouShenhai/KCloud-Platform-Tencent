/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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

package org.laokou.auth.server.infrastructure.convert;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.client.exception.CustomAuthExceptionHandler;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.auth.server.infrastructure.token.Oauth2PasswordAuthenticationToken;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.common.core.utils.StringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kou Shenhai
 */
//@Component
//@RequiredArgsConstructor
public class Oauth2PasswordAuthenticationConverter implements AuthenticationConverter {

//    private final LoginLogUtil loginLogUtil;
//
//    private final SysCaptchaService sysCaptchaService;

    @Override
    public Authentication convert(HttpServletRequest request) {
        String uuid = request.getParameter(AuthConstant.UUID);
        String captcha = request.getParameter(AuthConstant.CAPTCHA);
        String username = request.getParameter(OAuth2ParameterNames.USERNAME);
        String password = request.getParameter(OAuth2ParameterNames.PASSWORD);
        String scope = request.getParameter(OAuth2ParameterNames.SCOPE);
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (StringUtil.isEmpty(grantType)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("" + ErrorCode.INVALID_CLIENT
                    ,MessageUtil.getMessage(ErrorCode.INVALID_CLIENT)
                    ,CustomAuthExceptionHandler.ACCESS_TOKEN_REQUEST_ERROR_URI));
        }
        if (StringUtil.isEmpty(scope)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("" + ErrorCode.INVALID_SCOPE
                    ,MessageUtil.getMessage(ErrorCode.INVALID_SCOPE)
                    ,CustomAuthExceptionHandler.ACCESS_TOKEN_REQUEST_ERROR_URI));
        }
        if (StringUtil.isEmpty(uuid)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("" + ErrorCode.IDENTIFIER_NOT_NULL
                    ,MessageUtil.getMessage(ErrorCode.IDENTIFIER_NOT_NULL)
                    ,CustomAuthExceptionHandler.ACCESS_TOKEN_REQUEST_ERROR_URI));
        }
        if (StringUtil.isEmpty(captcha)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("" + ErrorCode.CAPTCHA_NOT_NULL
                    ,MessageUtil.getMessage(ErrorCode.CAPTCHA_NOT_NULL)
                    ,CustomAuthExceptionHandler.ACCESS_TOKEN_REQUEST_ERROR_URI));
        }
        if (StringUtil.isEmpty(username)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("" + ErrorCode.USERNAME_NOT_NULL
                    ,MessageUtil.getMessage(ErrorCode.USERNAME_NOT_NULL)
                    ,CustomAuthExceptionHandler.ACCESS_TOKEN_REQUEST_ERROR_URI));
        }
        if (StringUtil.isEmpty(password)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("" + ErrorCode.PASSWORD_NOT_NULL
                    ,MessageUtil.getMessage(ErrorCode.PASSWORD_NOT_NULL)
                    ,CustomAuthExceptionHandler.ACCESS_TOKEN_REQUEST_ERROR_URI));
        }
//        boolean validate = sysCaptchaService.validate(uuid, captcha);
//        if (!validate) {
//            loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR),request);
//            throw new OAuth2AuthenticationException(new OAuth2Error("" + ErrorCode.CAPTCHA_ERROR
//                    ,MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR)
//                    ,CustomAuthExceptionHandler.ACCESS_TOKEN_REQUEST_ERROR_URI));
//        }
        Map<String, Object> additionalParameters = new HashMap<>(2);
        additionalParameters.put(OAuth2ParameterNames.PASSWORD, request.getParameter(OAuth2ParameterNames.PASSWORD));
        additionalParameters.put(OAuth2ParameterNames.USERNAME, request.getParameter(OAuth2ParameterNames.USERNAME));
        // 获取当前已经认证的客户端
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        return new Oauth2PasswordAuthenticationToken(new AuthorizationGrantType(OAuth2ParameterNames.PASSWORD), clientPrincipal, additionalParameters);
    }
}
