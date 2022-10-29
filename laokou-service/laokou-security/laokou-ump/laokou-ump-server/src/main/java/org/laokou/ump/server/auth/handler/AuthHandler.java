package org.laokou.ump.server.auth.handler;

import org.laokou.auth.client.dto.LoginDTO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.constant.Constant;
import org.laokou.ump.server.auth.UsernamePasswordAuth;
import org.laokou.ump.server.auth.enums.LoginTypeEnum;
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

    public UserDetail getUserDetail(Authentication authentication) {
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
                String token = usernamePasswordAuth.getToken(loginDTO);
                return usernamePasswordAuth.getUserDetail(token);
            }
        }
        return null;
    }
}
