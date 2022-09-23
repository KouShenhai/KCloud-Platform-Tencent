package org.laokou.cas.filter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if (antPathMatcher.match(request.getServletPath(), OAUTH_URL)
                && request.getMethod().equalsIgnoreCase("POST")
                && GRANT_TYPE.equals(request.getParameter("grant_type"))) {
            filterChain.doFilter(request, response);
        }
    }

}
