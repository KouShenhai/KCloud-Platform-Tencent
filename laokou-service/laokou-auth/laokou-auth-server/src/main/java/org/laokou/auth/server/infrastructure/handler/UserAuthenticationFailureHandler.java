/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package org.laokou.auth.server.infrastructure.handler;

import lombok.SneakyThrows;
import org.laokou.auth.server.infrastructure.exception.CustomHttpResult;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.JacksonUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证失败处理器
 * @author Kou Shenhai
 */
@Component
public class UserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @SneakyThrows
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)  {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JacksonUtil.toJsonStr(new CustomHttpResult("" + ErrorCode.INTERNAL_SERVER_ERROR,e.getMessage())));
    }
}
