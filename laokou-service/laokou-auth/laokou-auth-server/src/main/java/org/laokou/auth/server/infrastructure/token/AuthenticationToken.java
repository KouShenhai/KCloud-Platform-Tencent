package org.laokou.auth.server.infrastructure.token;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * @author Kou Shenhai
 */
public interface AuthenticationToken {

    /**
     * 获取认证名称
     * @return
     */
    AuthorizationGrantType getGrantType();

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
