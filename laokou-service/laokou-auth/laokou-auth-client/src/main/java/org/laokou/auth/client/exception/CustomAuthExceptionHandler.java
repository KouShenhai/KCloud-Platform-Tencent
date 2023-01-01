/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
import jakarta.servlet.http.HttpServletResponse;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.common.swagger.utils.HttpResult;
import org.springframework.util.MimeTypeUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author laokou
 */
public class CustomAuthExceptionHandler {
    public static void handleException(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpStatus.HTTP_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(JacksonUtil.toJsonStr(new HttpResult().error(code,message)));
        writer.flush();
    }

}