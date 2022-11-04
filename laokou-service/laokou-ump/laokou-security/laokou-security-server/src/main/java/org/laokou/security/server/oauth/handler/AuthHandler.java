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
package org.laokou.security.server.oauth.handler;

import org.laokou.auth.client.dto.LoginDTO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.constant.Constant;
import org.laokou.security.server.oauth.enums.LoginTypeEnum;
import org.laokou.security.server.oauth.UsernamePasswordAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Kou Shenhai
 */
@Component
public class AuthHandler {

    @Autowired
    private UsernamePasswordAuth usernamePasswordAuth;

    public UserDetail userDetail(Authentication authentication) {
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        String code = username;
        LoginTypeEnum type = LoginTypeEnum.getType(Integer.valueOf(details.get(Constant.TYPE)));
        String uuid = details.get(Constant.UUID);
        String captcha = details.get(Constant.CAPTCHA);
        switch (type) {
            case USER_PASSWORD -> {
                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setCaptcha(captcha);
                loginDTO.setPassword(password);
                loginDTO.setUuid(uuid);
                loginDTO.setUsername(username);
                return usernamePasswordAuth.getUserDetail(loginDTO);
            }
        }
        return null;
    }
}
