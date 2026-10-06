package com.quick_cart.backend.infrastructure.redis.otp;

import com.quick_cart.backend.config.properties.OtpProperties;
import com.quick_cart.backend.identity.otp.OtpChallengeStore;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Repository
public class RedisOtpChallengeStore implements OtpChallengeStore {

    private static final String PREFIX =
        "quickcart:auth:otp:";

    private static final String HMAC_ALGORITHM =
        "HmacSHA256";

    private static final DefaultRedisScript<Long> CREATE_SCRIPT =
        new DefaultRedisScript<>(
            """
            local current = redis.call('GET', KEYS[1])

            if current then
                local _, _, _, issuedAt =
                    string.match(
                        current,
                        '^([^|]+)|([^|]+)|(%d+)|(%d+)$'
                    )

                if issuedAt then
                    local elapsed =
                        tonumber(ARGV[2]) - tonumber(issuedAt)

                    if elapsed < tonumber(ARGV[3]) then
                        return 0
                    end
                end
            end

            redis.call(
                'SET',
                KEYS[1],
                ARGV[1],
                'PX',
                ARGV[4]
            )

            return 1
            """,
            Long.class
        );

    private static final DefaultRedisScript<Long> VERIFY_SCRIPT =
        new DefaultRedisScript<>(
            """
            local current = redis.call('GET', KEYS[1])

            if not current then
                return -1
            end

            local challengeId, storedHash, attempts, issuedAt =
                string.match(
                    current,
                    '^([^|]+)|([^|]+)|(%d+)|(%d+)$'
                )

            if not challengeId then
                redis.call('DEL', KEYS[1])
                return -1
            end

            if challengeId ~= ARGV[1] then
                return -1
            end

            if storedHash == ARGV[2] then
                redis.call('DEL', KEYS[1])
                return 1
            end

            attempts = tonumber(attempts) + 1

            if attempts >= tonumber(ARGV[3]) then
                redis.call('DEL', KEYS[1])
                return 0
            end

            local ttl = redis.call('PTTL', KEYS[1])

            if ttl <= 0 then
                return -1
            end

            local updated =
                challengeId
                .. '|'
                .. storedHash
                .. '|'
                .. attempts
                .. '|'
                .. issuedAt

            redis.call(
                'SET',
                KEYS[1],
                updated,
                'PX',
                ttl
            )

            return 2
            """,
            Long.class
        );

    private final StringRedisTemplate redis;
    private final SecretKeySpec fingerprintKey;

    public RedisOtpChallengeStore(
        StringRedisTemplate redis,
        OtpProperties properties
    ) {
        this.redis = redis;

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

        this.fingerprintKey =
            new SecretKeySpec(
                secret,
                HMAC_ALGORITHM
            );
    }

    @Override
    public boolean create(
        String phoneE164,
        String challengeId,
        String otpHash,
        Duration expiry,
        Duration resendCooldown
    ) {
        long now =
            Instant.now().toEpochMilli();

        String payload =
            challengeId
                + "|"
                + otpHash
                + "|0|"
                + now;

        Long result = redis.execute(
            CREATE_SCRIPT,
            List.of(key(phoneE164)),
            payload,
            Long.toString(now),
            Long.toString(
                resendCooldown.toMillis()
            ),
            Long.toString(
                expiry.toMillis()
            )
        );

        return result != null && result == 1L;
    }

    @Override
    public VerificationResult verify(
        String phoneE164,
        String challengeId,
        String expectedHash,
        int maxAttempts
    ) {
        Long result = redis.execute(
            VERIFY_SCRIPT,
            List.of(key(phoneE164)),
            challengeId,
            expectedHash,
            Integer.toString(maxAttempts)
        );

        if (result == null || result == -1L) {
            return VerificationResult.NOT_FOUND;
        }

        if (result == 1L) {
            return VerificationResult.SUCCESS;
        }

        if (result == 0L) {
            return VerificationResult.ATTEMPTS_EXHAUSTED;
        }

        return VerificationResult.INVALID;
    }

    private String key(
        String phoneE164
    ) {
        return PREFIX + fingerprint(phoneE164);
    }

    private String fingerprint(
        String phoneE164
    ) {
        try {
            Mac mac =
                Mac.getInstance(HMAC_ALGORITHM);

            mac.init(fingerprintKey);

            byte[] digest = mac.doFinal(
                (
                    "redis-key|"
                        + phoneE164
                ).getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(digest);

        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                "Unable to derive Redis OTP key",
                exception
            );
        }
    }
}
