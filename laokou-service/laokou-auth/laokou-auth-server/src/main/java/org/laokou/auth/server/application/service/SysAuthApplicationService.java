/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
package org.laokou.auth.server.application.service;

import jakarta.servlet.http.HttpServletRequest;
import org.laokou.auth.server.infrastructure.server.AuthToken;

import java.io.IOException;

/**
 * auth服务
 * @author laokou
 */
public interface SysAuthApplicationService {

    /***
     * 用户登录
     * @param request
     * @throws IOException
     * @return
     */
    AuthToken login(HttpServletRequest request) throws IOException;

    /**
     * 生成验证码
     * @param request
     * @return
     */
    String captcha(HttpServletRequest request);

    /**
     * 退出登录
     * @param request
     * @return
     */
    Boolean logout(HttpServletRequest request);

}
