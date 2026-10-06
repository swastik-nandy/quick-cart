package com.quick_cart.backend.identity.otp;

import com.quick_cart.backend.config.properties.OtpProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

@Component
final class OtpHasher {

    private static final String ALGORITHM = "HmacSHA256";

    private final SecretKeySpec key;

    OtpHasher(
        OtpProperties properties
    ) {
        byte[] secret;

        try {
            secret = Base64.getDecoder()
                .decode(properties.hmacSecret());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "OTP_HMAC_SECRET must be Base64 encoded",
                exception
            );
        }

        if (secret.length < 32) {
            throw new IllegalArgumentException(
                "OTP_HMAC_SECRET must contain at least 256 bits"
            );
        }

        this.key = new SecretKeySpec(
            secret,
            ALGORITHM
        );
    }

    String hash(
        String challengeId,
        String phoneE164,
        String otp
    ) {
        String payload =
            challengeId + "|" + phoneE164 + "|" + otp;

        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);

            byte[] result = mac.doFinal(
                payload.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder()
                .encodeToString(result);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                "Unable to calculate OTP HMAC",
                exception
            );
        }
    }

    boolean matches(
        String storedHash,
        String challengeId,
        String phoneE164,
        String otp
    ) {
        byte[] stored = Base64.getDecoder()
            .decode(storedHash);

        byte[] candidate = Base64.getDecoder()
            .decode(
                hash(
                    challengeId,
                    phoneE164,
                    otp
                )
            );

        return MessageDigest.isEqual(
            stored,
            candidate
        );
    }
}
