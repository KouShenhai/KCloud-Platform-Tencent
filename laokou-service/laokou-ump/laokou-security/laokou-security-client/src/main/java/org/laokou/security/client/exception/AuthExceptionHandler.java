/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
package org.laokou.security.client.exception;

import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.common.utils.JacksonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author Kou Shenhai
 */
@Component
public class AuthExceptionHandler {

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (req,resp,ex) -> {
            resp.setStatus(ErrorCode.FORBIDDEN);
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
            PrintWriter writer = resp.getWriter();
            writer.write(JacksonUtil.toJsonStr(new HttpResultUtil().error(ErrorCode.FORBIDDEN)));
            writer.flush();
        };
    }

}
