package com.quick_cart.backend.identity.otp;

import java.time.Duration;

public interface OtpChallengeStore {

    boolean create(
        String phoneE164,
        String challengeId,
        String otpHash,
        Duration expiry,
        Duration resendCooldown
    );

    VerificationResult verify(
        String phoneE164,
        String challengeId,
        String expectedHash,
        int maxAttempts
    );

    enum VerificationResult {
        SUCCESS,
        INVALID,
        ATTEMPTS_EXHAUSTED,
        NOT_FOUND
    }
}
