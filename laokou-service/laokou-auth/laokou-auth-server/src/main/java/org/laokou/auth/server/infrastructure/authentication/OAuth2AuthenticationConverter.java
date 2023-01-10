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
import org.laokou.common.core.utils.HashUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 密码模式
 * @author laokou
 */
public class OAuth2AuthenticationConverter implements AuthenticationConverter {

    /**
     * 密码/手机/邮箱
     */
    private static final List<String> grantTypes = List.of(
            OAuth2PasswordAuthenticationProvider.GRANT_TYPE
            , OAuth2SmsAuthenticationProvider.GRANT_TYPE
            , OAuth2EmailAuthenticationProvider.GRANT_TYPE);

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        // 密码模式
        if (!grantTypes.contains(grantType)) {
            return null;
        }
        // 获取上下文认证信息
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        // 构建请求参数集合
        MultiValueMap<String, String> parameters = HashUtil.getParameters(request);
        Map<String, Object> additionalParameters = new HashMap<>(parameters.size());
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
                    !key.equals(OAuth2ParameterNames.CLIENT_ID)) {
                additionalParameters.put(key, value.get(0));
            }
        });
        return new OAuth2AuthenticationToken(grantType,clientPrincipal, additionalParameters);
    }
}
