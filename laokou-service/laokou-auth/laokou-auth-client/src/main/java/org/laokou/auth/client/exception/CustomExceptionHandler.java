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

package org.laokou.auth.client.exception;

import org.laokou.common.core.utils.JacksonUtil;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author Kou Shenhai
 */
public class CustomExceptionHandler {

    public static void handleException(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(cn.hutool.http.HttpStatus.HTTP_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(JacksonUtil.toJsonStr(new CustomHttpResult(code,message)));
        writer.flush();
    }

    public static String getMsg(String code,String message) {
        return switch (code) {
            case OAuth2Exception.INVALID_CLIENT -> "无效客户端凭据";
            case OAuth2Exception.UNAUTHORIZED_CLIENT -> "未经授权的客户端";
            case OAuth2Exception.INVALID_GRANT -> "无效授权";
            case OAuth2Exception.INVALID_SCOPE -> "无效作用域";
            case OAuth2Exception.INVALID_TOKEN -> "无效令牌";
            case OAuth2Exception.INVALID_REQUEST -> "无效请求";
            case OAuth2Exception.REDIRECT_URI_MISMATCH -> "错误重定向地址";
            case OAuth2Exception.UNSUPPORTED_GRANT_TYPE -> "不支持的认证模式";
            case OAuth2Exception.UNSUPPORTED_RESPONSE_TYPE -> "不支持的资源类型";
            case OAuth2Exception.ACCESS_DENIED -> "访问被拒绝";
            default -> message;
        };
    }

}
