/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
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

import com.alipay.api.AlipayApiException;
import org.laokou.auth.client.dto.LoginDTO;
import org.laokou.auth.client.user.BaseUserVO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.client.vo.LoginVO;
import org.laokou.auth.client.vo.UserInfoVO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * auth服务
 * @author Kou Shenhai
 */
public interface SysAuthApplicationService {

    /**
     * 登录
     * @param loginDTO
     * @return
     * @throws Exception
     */
    LoginVO login(LoginDTO loginDTO) throws Exception;

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

    /**
     * 访问资源权限
     * @param token
     * @param uri
     * @param method
     * @return
     */
    BaseUserVO resource(String token, String uri, String method);

    /***
     * 用户详细信息
     * @param request
     * @return
     */
    UserDetail userDetail(HttpServletRequest request);

    /**
     * 获取用户信息
     * @param request
     * @return
     */
    UserInfoVO userInfo(HttpServletRequest request);

    /**
     * 获取对外开放用户信息
     * @param request
     * @return
     */
    BaseUserVO openUserInfo(HttpServletRequest request);

    /**
     * 支付宝登录
     * @param request
     * @param response
     * @throws AlipayApiException
     * @throws IOException
     */
    void zfbLogin(HttpServletRequest request,HttpServletResponse response) throws Exception;

    void zfbBind(HttpServletRequest request,HttpServletResponse response) throws IOException;

    /**
     * 开放登录
     * @param response
     * @param request
     * @throws Exception
     */
    void openLogin(HttpServletResponse response, HttpServletRequest request) throws Exception;
}
