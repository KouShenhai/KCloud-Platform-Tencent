/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.auth.server.infrastructure.authentication;
import jakarta.servlet.http.HttpServletRequest;
import org.laokou.auth.client.exception.CustomAuthExceptionHandler;
import org.laokou.common.core.utils.HashUtil;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.common.swagger.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
/**
 * 密码模式
 * @author laokou
 */
public abstract class OAuth2BaseAuthenticationConverter implements AuthenticationConverter {

    /**
     * 类型
     * @return
     */
    abstract String getGrantType();

    /**
     * 转换
     * @param clientPrincipal
     * @param additionalParameters
     * @return
     */
    abstract Authentication convert(Authentication clientPrincipal,Map<String, Object> additionalParameters);

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!getGrantType().equals(grantType)) {
            return null;
        }
        // 构建请求参数集合
        MultiValueMap<String, String> parameters = HashUtil.getParameters(request);
        // 判断scope
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            CustomAuthExceptionHandler.throwError(ErrorCode.INVALID_SCOPE, MessageUtil.getMessage(ErrorCode.INVALID_SCOPE));
        }
        // 获取上下文认证信息
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> additionalParameters = new HashMap<>(parameters.size());
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
                    !key.equals(OAuth2ParameterNames.CLIENT_ID)) {
                additionalParameters.put(key, value.get(0));
            }
        });
        return convert(clientPrincipal,additionalParameters);
    }

}
