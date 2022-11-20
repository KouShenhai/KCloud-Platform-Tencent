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

import cn.hutool.http.HttpStatus;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.common.core.utils.MessageUtil;
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
        response.setStatus(HttpStatus.HTTP_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(JacksonUtil.toJsonStr(new CustomHttpResult(code,message)));
        writer.flush();
    }

    public static String getMsg(String code,String message) {
        return switch (code) {
            case OAuth2Exception.INVALID_CLIENT ->  MessageUtil.getMessage(ErrorCode.INVALID_CLIENT);
            case OAuth2Exception.UNAUTHORIZED_CLIENT ->  MessageUtil.getMessage(ErrorCode.UNAUTHORIZED_CLIENT);
            case OAuth2Exception.INVALID_GRANT ->  MessageUtil.getMessage(ErrorCode.INVALID_GRANT);
            case OAuth2Exception.INVALID_SCOPE ->  MessageUtil.getMessage(ErrorCode.INVALID_SCOPE);
            case OAuth2Exception.INVALID_TOKEN ->  MessageUtil.getMessage(ErrorCode.INVALID_TOKEN);
            case OAuth2Exception.INVALID_REQUEST ->  MessageUtil.getMessage(ErrorCode.INVALID_REQUEST);
            case OAuth2Exception.REDIRECT_URI_MISMATCH ->  MessageUtil.getMessage(ErrorCode.REDIRECT_URI_MISMATCH);
            case OAuth2Exception.UNSUPPORTED_GRANT_TYPE ->  MessageUtil.getMessage(ErrorCode.UNSUPPORTED_GRANT_TYPE);
            case OAuth2Exception.UNSUPPORTED_RESPONSE_TYPE ->  MessageUtil.getMessage(ErrorCode.UNSUPPORTED_RESPONSE_TYPE);
            case OAuth2Exception.ACCESS_DENIED -> MessageUtil.getMessage(ErrorCode.ACCESS_DENIED);
            default -> message;
        };
    }

}