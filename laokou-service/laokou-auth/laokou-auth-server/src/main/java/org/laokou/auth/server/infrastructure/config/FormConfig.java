package org.laokou.auth.server.infrastructure.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * 基于授权码模式
 * @author laokou
 */
public final class FormConfig extends AbstractHttpConfigurer<FormConfig, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.formLogin()
                .and()
                .csrf()
                .disable();
    }

}
