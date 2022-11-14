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
package org.laokou.auth.server.application.service;

import org.laokou.auth.client.user.UserDetail;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * auth服务
 * @author Kou Shenhai
 */
public interface SysAuthApplicationService {

    /***
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    UserDetail login(String username,String password);

    /**
     * 退出
     * @param request
     * @return
     */
    void logout(HttpServletRequest request);

    /**
     * 生成验证码
     * @param uuid
     * @param response
     * @throws IOException
     */
    void captcha(String uuid, HttpServletResponse response) throws IOException;


}
