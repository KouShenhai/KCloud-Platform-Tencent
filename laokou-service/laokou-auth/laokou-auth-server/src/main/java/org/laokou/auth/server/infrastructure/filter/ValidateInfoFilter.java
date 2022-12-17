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
package org.laokou.auth.server.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.laokou.auth.client.exception.CustomAuthExceptionHandler;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.server.infrastructure.handler.CustomAuthenticationFailureHandler;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.common.core.utils.StringUtil;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/4/11 0011 下午 2:29
 */
@Component
@AllArgsConstructor
public class ValidateInfoFilter extends OncePerRequestFilter {

    private final static AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final static String OAUTH_URL = "/oauth/token";
    private final SysCaptchaService sysCaptchaService;

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    private final LoginLogUtil loginLogUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (ANT_PATH_MATCHER.match(request.getServletPath(), OAUTH_URL)
                && request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())
                && OAuth2ParameterNames.PASSWORD.equals(request.getParameter(OAuth2ParameterNames.GRANT_TYPE))) {

            try {
                //validate(scope,uuid, captcha,username,password,request);
            } catch (AuthenticationException e) {
                //失败处理器
                customAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void validate(String scope,String uuid, String captcha, String username, String password, HttpServletRequest request) {
        if (StringUtil.isEmpty(scope)) {
            throw new BadCredentialsException(CustomAuthExceptionHandler.getMsg(OAuth2ErrorCodes.INVALID_SCOPE));
        }
        if (StringUtil.isEmpty(uuid)) {
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.IDENTIFIER_NOT_NULL));
        }
        if (StringUtil.isEmpty(captcha)) {
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.CAPTCHA_NOT_NULL));
        }
        if (StringUtil.isEmpty(username)) {
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.USERNAME_NOT_NULL));
        }
        if (StringUtil.isEmpty(password)) {
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.PASSWORD_NOT_NULL));
        }
        boolean validate = sysCaptchaService.validate(uuid, captcha);
        if (!validate) {
            loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR),request);
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR));
        }
    }

}
