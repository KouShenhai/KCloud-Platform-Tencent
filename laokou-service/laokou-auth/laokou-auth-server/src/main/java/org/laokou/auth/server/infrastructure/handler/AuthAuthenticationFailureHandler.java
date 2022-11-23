/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package org.laokou.auth.server.infrastructure.handler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.client.exception.CustomExceptionHandler;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 认证失败处理器
 * @author Kou Shenhai
 */
@Component
@Slf4j
public class AuthAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @SneakyThrows
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)  {
        log.error("错误信息：{}",e.getMessage());
        CustomExceptionHandler.handleException(response, OAuth2Exception.ERROR,e.getMessage());
    }
}
