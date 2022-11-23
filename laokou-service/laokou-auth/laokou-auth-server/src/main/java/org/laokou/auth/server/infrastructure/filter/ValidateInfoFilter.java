package org.laokou.auth.server.infrastructure.filter;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.server.infrastructure.handler.UserAuthenticationFailureHandler;
import org.laokou.auth.server.infrastructure.log.AuthLogUtil;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.common.core.utils.StringUtil;
import org.laokou.common.core.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
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
public class ValidateInfoFilter extends OncePerRequestFilter {

    private final static AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final static String OAUTH_URL = "/oauth/token";

    private final static String GRANT_TYPE_NAME = "grant_type";

    private final static String GRANT_TYPE = AuthConstant.PASSWORD;

    @Autowired
    private SysCaptchaService sysCaptchaService;

    @Autowired
    private UserAuthenticationFailureHandler userAuthenticationFailureHandler;

    @Autowired
    private AuthLogUtil authLogUtil;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if (ANT_PATH_MATCHER.match(request.getServletPath(), OAUTH_URL)
                && request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())
                && GRANT_TYPE.equals(request.getParameter(GRANT_TYPE_NAME))) {
            String uuid = request.getParameter(AuthConstant.UUID);
            String captcha = request.getParameter(AuthConstant.CAPTCHA);
            String username = request.getParameter(AuthConstant.USERNAME);
            String password = request.getParameter(AuthConstant.PASSWORD);
            try {
                validate(uuid, captcha,username,password,request);
            } catch (AuthenticationException e) {
                //失败处理器
                userAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void validate(String uuid,String captcha,String username,String password,HttpServletRequest request) {
        if (StringUtil.isEmpty(uuid)) {
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.IDENTIFIER_NOT_NULL));
        }
        if (StringUtil.isEmpty(captcha)) {
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.CAPTCHA_NOT_NULL));
        }
        if (StringUtil.isEmpty(username)) {
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.USERNAME_NOT_NULL));
        }
        if (StringUtil.isEmpty(password)) {
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.PASSWORD_NOT_NULL));
        }
        boolean validate = sysCaptchaService.validate(uuid, captcha);
        if (!validate) {
            ThreadUtil.executorService.execute(() -> authLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR),request));
            throw new BadCredentialsException(MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR));
        }
    }

}
