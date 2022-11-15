package org.laokou.auth.server.infrastructure.filter;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.infrastructure.constant.OauthConstant;
import org.laokou.auth.server.infrastructure.handler.UserAuthenticationFailureHandler;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.common.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/4/11 0011 下午 2:29
 */
@Component
@AllArgsConstructor
public class ValidateCodeFilter extends OncePerRequestFilter {

    private final static AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final static String OAUTH_URL = "/oauth/token";

    private final static String GRANT_TYPE = "password";

    @Autowired
    private SysCaptchaService sysCaptchaService;

    @Autowired
    private UserAuthenticationFailureHandler userAuthenticationFailureHandler;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if (antPathMatcher.match(request.getServletPath(), OAUTH_URL)
                && request.getMethod().equalsIgnoreCase("POST")
                && GRANT_TYPE.equals(request.getParameter("grant_type"))) {
            String uuid = request.getParameter(OauthConstant.UUID);
            String captcha = request.getParameter(OauthConstant.CAPTCHA);
            try {
                validate(uuid, captcha);
            } catch (AuthenticationException e) {
                //失败处理器
                userAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void validate(String uuid,String captcha) {
        if (StringUtil.isEmpty(uuid)) {
            throw new CredentialsExpiredException("唯一标识符不能为空");
        }
        if (StringUtil.isEmpty(captcha)) {
            throw new CredentialsExpiredException("验证码不能为空");
        }
        boolean validate = sysCaptchaService.validate(uuid, captcha);
        if (!validate) {
            throw new CredentialsExpiredException(MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR));
        }
    }

}
