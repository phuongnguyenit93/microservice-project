package com.phuong.discoveryserver.config;

import org.springframework.security.crypto.password.PasswordEncoder;

public final class NoEncoderPassword implements PasswordEncoder {
    private static final PasswordEncoder INSTANCE = new NoEncoderPassword();

    private NoEncoderPassword() {

    }

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }

    public static PasswordEncoder getInstance() {
        return INSTANCE;
    }
}
