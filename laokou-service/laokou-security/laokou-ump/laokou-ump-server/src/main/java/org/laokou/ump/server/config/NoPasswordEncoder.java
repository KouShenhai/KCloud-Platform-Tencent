package org.laokou.ump.server.config;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Kou Shenhai
 */
public final class NoPasswordEncoder implements PasswordEncoder {
    private static final PasswordEncoder INSTANCE = new NoPasswordEncoder();

    private NoPasswordEncoder() {
    }

    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }

    public static PasswordEncoder getInstance() {
        return INSTANCE;
    }
}