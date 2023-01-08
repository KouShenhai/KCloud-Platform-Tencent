package org.laokou.auth.server.infrastructure.server;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * 认证流程
 *
 * 1.UsernamePasswordAuthenticationToken -> AuthenticationManager
 * 2.AuthenticationManager -> ProviderManager
 * 3.ProviderManager -> AuthenticationProviders
 * 4.AuthenticationProviders -> DaoAuthenticationProvider
 * 5.DaoAuthenticationProvider -> UserDetailsService
 * 6.UserDetailsService -> PasswordEncoder
 * 7.AuthenticationManager -> UsernamePasswordAuthenticationToken
 *
 * @author laokou
 */
public interface AuthenticationServer {

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
