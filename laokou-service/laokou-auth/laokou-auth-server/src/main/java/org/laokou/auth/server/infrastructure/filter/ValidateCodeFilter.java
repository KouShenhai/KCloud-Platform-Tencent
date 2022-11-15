package org.laokou.auth.server.infrastructure.filter;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.infrastructure.constant.OauthConstant;
import org.laokou.auth.server.infrastructure.handler.UserAuthenticationFailureHandler;
import org.laokou.auth.server.infrastructure.log.AuthLogUtil;
import org.laokou.common.core.enums.ResultStatusEnum;
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
import java.io.IOException;

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

    @Autowired
    private AuthLogUtil authLogUtil;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if (antPathMatcher.match(request.getServletPath(), OAUTH_URL)
                && request.getMethod().equalsIgnoreCase("POST")
                && GRANT_TYPE.equals(request.getParameter("grant_type"))) {
            String uuid = request.getParameter(OauthConstant.UUID);
            String captcha = request.getParameter(OauthConstant.CAPTCHA);
            String username = request.getParameter(OauthConstant.USERNAME);
            String password = request.getParameter(OauthConstant.PASSWORD);
            try {
                validate(uuid, captcha,username,password);
            } catch (AuthenticationException e) {
                //失败处理器
                userAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void validate(String uuid,String captcha,String username,String password) throws IOException {
        if (StringUtil.isEmpty(uuid)) {
            throw new CredentialsExpiredException("唯一标识符不能为空");
        }
        if (StringUtil.isEmpty(captcha)) {
            throw new CredentialsExpiredException("验证码不能为空");
        }
        if (StringUtil.isEmpty(username)) {
            throw new CredentialsExpiredException("用户名不能为空");
        }
        if (StringUtil.isEmpty(password)) {
            throw new CredentialsExpiredException("密码不能为空");
        }
        boolean validate = sysCaptchaService.validate(uuid, captcha);
        if (!validate) {
            authLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR));
            throw new CredentialsExpiredException(MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR));
        }
    }

}
