/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.auth.server.infrastructure.common.filter;

import org.laokou.auth.client.user.SecurityUser;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.laokou.common.utils.JacksonUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * auth过滤器
 * @author Kou Shenhai
 */
@Slf4j
public class AuthFilter extends AuthenticatingFilter {

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response){
        //获取请求token
        String Authorization = SecurityUser.getAuthorization((HttpServletRequest) request);
        if(StringUtils.isBlank(Authorization)){
            return null;
        }
        return new AuthToken(Authorization);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if(((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name())){
            return true;
        }
        String pathWithinApplication = getPathWithinApplication(request);
        log.info(pathWithinApplication);
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token，如果token为空，直接返回401
        String Authorization = SecurityUser.getAuthorization((HttpServletRequest) request);
        log.info("Authorization:{}",Authorization);
        if(StringUtils.isBlank(Authorization)){
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String json = JacksonUtil.toJsonStr(new HttpResultUtil<Boolean>().error(ErrorCode.UNAUTHORIZED));
            httpResponse.getWriter().print(json);
            return false;
        }
        return executeLogin(request, response);
    }

    @SneakyThrows
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //处理登录失败的异常
        String json = JacksonUtil.toJsonStr(new HttpResultUtil().error(ErrorCode.AUTHORIZATION_INVALID, e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
        httpResponse.getWriter().print(json);
        return false;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, CorsConfiguration.ALL);
        httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, CorsConfiguration.ALL);
        httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, CorsConfiguration.ALL);
        httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.SC_OK);
            return false;
        }
        return super.preHandle(request, response);
    }

}
