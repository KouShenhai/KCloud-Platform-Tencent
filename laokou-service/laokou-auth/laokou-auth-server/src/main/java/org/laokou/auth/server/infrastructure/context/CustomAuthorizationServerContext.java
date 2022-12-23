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
package org.laokou.auth.server.infrastructure.context;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author laokou
 */
@RequiredArgsConstructor
public class CustomAuthorizationServerContext implements AuthorizationServerContext {

    private final HttpServletRequest request;

    private final AuthorizationServerSettings authorizationServerSettings;

    @Override
    public String getIssuer() {
        return this.authorizationServerSettings.getIssuer() != null ?
                this.authorizationServerSettings.getIssuer() :
                getContextPath(this.request);
    }

    @Override
    public AuthorizationServerSettings getAuthorizationServerSettings() {
        return this.authorizationServerSettings;
    }

    private static String getContextPath(HttpServletRequest request) {
        // @formatter:off
        return UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .build()
                .toUriString();
        // @formatter:on
    }
}
