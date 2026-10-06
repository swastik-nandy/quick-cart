package com.quick_cart.backend.identity.otp;

import com.quick_cart.backend.config.properties.OtpProperties;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
final class PhoneOtpChallengeEngine {

    private static final Pattern E164 =
        Pattern.compile("^\\+[1-9][0-9]{7,14}$");

    private final OtpProperties properties;
    private final OtpCodeGenerator generator;
    private final OtpHasher hasher;
    private final OtpChallengeStore store;

    PhoneOtpChallengeEngine(
        OtpProperties properties,
        OtpCodeGenerator generator,
        OtpHasher hasher,
        OtpChallengeStore store
    ) {
        this.properties = properties;
        this.generator = generator;
        this.hasher = hasher;
        this.store = store;
    }

    GeneratedChallenge issue(
        String phoneE164
    ) {
        validatePhone(phoneE164);

        String challengeId =
            UUID.randomUUID().toString();

        String otp =
            generator.generate(properties.digits());

        String otpHash =
            hasher.hash(
                challengeId,
                phoneE164,
                otp
            );

        boolean created = store.create(
            phoneE164,
            challengeId,
            otpHash,
            properties.expiry(),
            properties.resendCooldown()
        );

        if (!created) {
            throw new OtpResendCooldownException();
        }

        return new GeneratedChallenge(
            challengeId,
            otp,
            properties.expiry()
        );
    }

    VerificationResult verify(
        String phoneE164,
        String challengeId,
        String otp
    ) {
        validatePhone(phoneE164);

        String expectedHash =
            hasher.hash(
                challengeId,
                phoneE164,
                otp
            );

        OtpChallengeStore.VerificationResult result =
            store.verify(
                phoneE164,
                challengeId,
                expectedHash,
                properties.maxAttempts()
            );

        return switch (result) {
            case SUCCESS ->
                VerificationResult.success(phoneE164);

            case INVALID ->
                VerificationResult.invalid();

            case ATTEMPTS_EXHAUSTED ->
                VerificationResult.exhausted();

            case NOT_FOUND ->
                VerificationResult.expired();
        };
    }

    private static void validatePhone(
        String phoneE164
    ) {
        if (
            phoneE164 == null
                || !E164.matcher(phoneE164).matches()
        ) {
            throw new IllegalArgumentException(
                "Phone number must be in E.164 format"
            );
        }
    }

    record GeneratedChallenge(
        String challengeId,
        String otp,
        Duration expiresIn
    ) {
    }

    record VerificationResult(
        Status status,
        String phoneE164
    ) {

        static VerificationResult success(
            String phone
        ) {
            return new VerificationResult(
                Status.SUCCESS,
                phone
            );
        }

        static VerificationResult invalid() {
            return new VerificationResult(
                Status.INVALID,
                null
            );
        }

        static VerificationResult expired() {
            return new VerificationResult(
                Status.EXPIRED_OR_NOT_FOUND,
                null
            );
        }

        static VerificationResult exhausted() {
            return new VerificationResult(
                Status.ATTEMPTS_EXHAUSTED,
                null
            );
        }
    }

    enum Status {
        SUCCESS,
        INVALID,
        EXPIRED_OR_NOT_FOUND,
        ATTEMPTS_EXHAUSTED
    }

    static final class OtpResendCooldownException
            extends RuntimeException {

        OtpResendCooldownException() {
            super(
                "OTP resend cooldown is active"
            );
        }
    }
}
