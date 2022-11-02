package org.laokou.ump.client.exception;

import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Kou Shenhai
 */
@RestControllerAdvice
@ResponseBody
@Component
public class AuthExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler({AccessDeniedException.class})
    public HttpResultUtil<Boolean> handleRenException(){
        return new HttpResultUtil<Boolean>().error(ErrorCode.FORBIDDEN);
    }

}
