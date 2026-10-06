package com.quick_cart.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "quickcart.auth.otp")
public record OtpProperties(
    Duration expiry,
    int maxAttempts,
    Duration resendCooldown,
    int digits,
    String hmacSecret
) {

    public OtpProperties {
        if (expiry == null || expiry.isZero() || expiry.isNegative()) {
            throw new IllegalArgumentException(
                "OTP expiry must be greater than zero"
            );
        }

        if (maxAttempts <= 0) {
            throw new IllegalArgumentException(
                "OTP max attempts must be greater than zero"
            );
        }

        if (resendCooldown == null || resendCooldown.isNegative()) {
            throw new IllegalArgumentException(
                "OTP resend cooldown cannot be negative"
            );
        }

        if (digits < 6 || digits > 8) {
            throw new IllegalArgumentException(
                "OTP digits must be between 6 and 8"
            );
        }

        if (hmacSecret == null || hmacSecret.isBlank()) {
            throw new IllegalArgumentException(
                "OTP HMAC secret must be configured"
            );
        }
    }
}
