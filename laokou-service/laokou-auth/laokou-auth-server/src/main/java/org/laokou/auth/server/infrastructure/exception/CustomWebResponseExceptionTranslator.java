/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.auth.server.infrastructure.exception;
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.client.exception.CustomAuthExceptionHandler;
import org.laokou.auth.client.exception.CustomHttpResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * 登录或者鉴权失败时的返回信息
 * SpringSecurity最新版本更新
 * @author Kou Shenhai
 */
@Slf4j
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {
    private final ThrowableAnalyzer THROWABLE_ANALYZER = new DefaultThrowableAnalyzer();

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) {
        Throwable[] causeChain = THROWABLE_ANALYZER.determineCauseChain(e);
        Exception exception = (AuthenticationException) THROWABLE_ANALYZER.getFirstThrowableOfType(AuthenticationException.class, causeChain);
        if (exception != null) {
            return handleOauth2Exception(new CustomOauth2Exception(e.getMessage(), e));
        }
        exception = (AccessDeniedException) THROWABLE_ANALYZER.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
        if (exception != null) {
            return handleOauth2Exception(new CustomOauth2Exception(exception.getMessage(), exception));
        }
        exception = (InvalidGrantException) THROWABLE_ANALYZER.getFirstThrowableOfType(InvalidGrantException.class, causeChain);
        if (exception != null) {
            return handleOauth2Exception(new CustomOauth2Exception(exception.getMessage(), exception));
        }
        exception = (HttpRequestMethodNotSupportedException) THROWABLE_ANALYZER.getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
        if (exception != null) {
            return handleOauth2Exception(new CustomOauth2Exception(exception.getMessage(), exception));
        }
        exception = (OAuth2Exception) THROWABLE_ANALYZER.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
        if (exception != null) {
            return handleOauth2Exception((OAuth2Exception) exception);
        }
        return handleOauth2Exception(new CustomOauth2Exception(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));
    }

    /**
     * 自定义异常响应
     * 验证grant_type、client_secret、client_id，参考{@link OAuth2Exception#create(String, String)}
     * @param e
     * @return
     */
    private ResponseEntity<OAuth2Exception> handleOauth2Exception(OAuth2Exception e) {
        int status = e.getHttpErrorCode();
        String code = e.getOAuth2ErrorCode();
        String message = CustomAuthExceptionHandler.getMsg(code,e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
        headers.set(HttpHeaders.PRAGMA, "no-cache");
        if (status == HttpStatus.UNAUTHORIZED.value() || (e instanceof InsufficientScopeException)) {
            headers.set(HttpHeaders.WWW_AUTHENTICATE, String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));
        }
        log.error("错误码：{},错误信息：{}",code,message);
        return new ResponseEntity(new CustomHttpResult(code,message), headers, HttpStatus.OK);
    }

}
