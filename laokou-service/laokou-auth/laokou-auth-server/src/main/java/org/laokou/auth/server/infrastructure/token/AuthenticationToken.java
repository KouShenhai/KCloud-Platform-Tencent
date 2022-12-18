package org.laokou.auth.server.infrastructure.token;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * @author Kou Shenhai
 */
public interface AuthenticationToken {

    /**
     * 登录
     * @param request
     * @return
     */
    UsernamePasswordAuthenticationToken login(HttpServletRequest request);

    /**
     * 验证码
     * @param request
     * @return
     */
    String captcha(HttpServletRequest request);

}
