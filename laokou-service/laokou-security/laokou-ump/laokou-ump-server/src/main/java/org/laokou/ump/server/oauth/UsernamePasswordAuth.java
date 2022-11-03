/**
 * Copyright (c) 2022 KCloud-Platform-Netflix Authors. All Rights Reserved.
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
package org.laokou.ump.server.oauth;

import org.laokou.auth.client.dto.LoginDTO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.common.utils.MessageUtil;
import org.laokou.ump.server.exception.CustomOAuth2Exception;
import org.laokou.ump.server.feign.auth.AuthApiFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @author Kou Shenhai
 */
@Component
public class UsernamePasswordAuth{

    @Autowired
    private AuthApiFeignClient authApiFeignClient;

    public UserDetail getUserDetail(LoginDTO loginDTO) {
        HttpResultUtil<UserDetail> result;
        try {
            result = authApiFeignClient.userDetail(loginDTO);
        } catch (Exception e) {
            throw new CustomOAuth2Exception(ErrorCode.SERVICE_MAINTENANCE, MessageUtil.getMessage(ErrorCode.SERVICE_MAINTENANCE));
        }
        if (result.getCode() != Constant.SUCCESS) {
            throw new CustomOAuth2Exception(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

}
