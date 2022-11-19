package org.laokou.auth.server.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.client.exception.CustomExceptionHandler;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kou Shenhai
 */
@Component("customAuthenticationEntryPoint")
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("错误信息：{}",authException.getMessage());
        CustomExceptionHandler.handleException(response, OAuth2Exception.INVALID_CLIENT,CustomExceptionHandler.getMsg(OAuth2Exception.INVALID_CLIENT,""));
    }
}