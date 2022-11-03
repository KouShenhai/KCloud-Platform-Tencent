package org.laokou.ump.client.exception;

import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.common.utils.JacksonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author Kou Shenhai
 */
@Component
public class AuthExceptionHandler {

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (req,resp,ex) -> {
            resp.setStatus(ErrorCode.FORBIDDEN);
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
            PrintWriter writer = resp.getWriter();
            writer.write(JacksonUtil.toJsonStr(new HttpResultUtil().error(ErrorCode.FORBIDDEN)));
            writer.flush();
        };
    }

}
