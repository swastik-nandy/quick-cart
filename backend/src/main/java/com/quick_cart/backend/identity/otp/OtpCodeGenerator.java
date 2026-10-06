package com.quick_cart.backend.identity.otp;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Locale;

@Component
final class OtpCodeGenerator {

    private final SecureRandom secureRandom =
        new SecureRandom();

    String generate(int digits) {
        int bound = 1;

        for (int i = 0; i < digits; i++) {
            bound *= 10;
        }

        int value = secureRandom.nextInt(bound);

        return String.format(
            Locale.ROOT,
            "%0" + digits + "d",
            value
        );
    }
}
