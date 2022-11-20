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
package org.laokou.auth.server.infrastructure.constant;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/10 0010 下午 4:28
 */
public interface OauthConstant {
   /**
    * 标识
    */
   String UUID = "uuid";
   /**
    * 验证码
    */
   String CAPTCHA = "captcha";
   /**
    * 账号
    */
   String USERNAME = "username";
   /**
    * 密码
    */
   String PASSWORD = "password";
   /**
    * 登录成功
    */
   String LOGIN_SUCCESS_MSG = "登录成功";

   String SELECT_STATEMENT = "select client_id,client_secret,resource_ids, scope,authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity,refresh_token_validity, additional_information, autoapprove from boot_sys_oauth_client_details where client_id = ?";

   String FIND_STATEMENT = "select client_id,client_secret,resource_ids, scope,authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity,refresh_token_validity, additional_information, autoapprove from boot_sys_oauth_client_details order by client_id";
}
