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
package org.laokou.auth.server.infrastructure.handler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.client.exception.CustomAuthExceptionHandler;
import org.laokou.common.core.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证失败处理器
 * @author Kou Shenhai
 */
@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler, AuthenticationEntryPoint {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.error("错误信息：{}",e.getMessage());
        CustomAuthExceptionHandler.handleException(response, ErrorCode.INTERNAL_SERVER_ERROR,e.getMessage());
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.error("错误信息：{}",e.getMessage());
        CustomAuthExceptionHandler.handleException(response, ErrorCode.INTERNAL_SERVER_ERROR,e.getMessage());
    }
}
