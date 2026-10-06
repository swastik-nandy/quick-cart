package com.quick_cart.backend.identity.otp;

import com.quick_cart.backend.identity.domain.AuthenticationIdentity;
import com.quick_cart.backend.identity.domain.AuthenticationProvider;
import com.quick_cart.backend.identity.domain.User;
import com.quick_cart.backend.identity.otp.delivery.OtpDeliveryPort;
import com.quick_cart.backend.identity.repository.AuthenticationIdentityRepository;
import com.quick_cart.backend.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class PhoneOtpAuthService {

    private final PhoneOtpChallengeEngine engine;
    private final OtpDeliveryPort delivery;
    private final UserRepository users;
    private final AuthenticationIdentityRepository identities;

    public PhoneOtpAuthService(
        PhoneOtpChallengeEngine engine,
        OtpDeliveryPort delivery,
        UserRepository users,
        AuthenticationIdentityRepository identities
    ) {
        this.engine = engine;
        this.delivery = delivery;
        this.users = users;
        this.identities = identities;
    }

    public OtpRequested request(String phoneE164) {
        var challenge = engine.issue(phoneE164);

        delivery.send(
            phoneE164,
            challenge.otp(),
            challenge.expiresIn()
        );

        return new OtpRequested(
            challenge.challengeId(),
            challenge.expiresIn()
        );
    }

    @Transactional
    public VerifiedUser verify(
        String phoneE164,
        String challengeId,
        String otp
    ) {
        var result = engine.verify(
            phoneE164,
            challengeId,
            otp
        );

        if (
            result.status()
                != PhoneOtpChallengeEngine.Status.SUCCESS
        ) {
            throw new OtpVerificationException(
                switch (result.status()) {
                    case INVALID -> VerificationFailure.INVALID;
                    case EXPIRED_OR_NOT_FOUND -> VerificationFailure.EXPIRED_OR_NOT_FOUND;
                    case ATTEMPTS_EXHAUSTED -> VerificationFailure.ATTEMPTS_EXHAUSTED;
                    case SUCCESS -> throw new IllegalStateException(
                        "Successful OTP verification cannot produce an error"
                    );
                }
            );
        }

        String phone = result.phoneE164();

        var existingIdentity =
            identities.findByProviderAndProviderSubject(
                AuthenticationProvider.PHONE,
                phone
            );

        if (existingIdentity.isPresent()) {
            AuthenticationIdentity identity =
                existingIdentity.get();

            identity.recordAuthentication();

            return new VerifiedUser(
                identity.getUser().getId(),
                false
            );
        }

        User user = users.findByPhoneE164(phone)
            .orElseGet(
                () -> users.save(
                    User.createPhoneUser(phone)
                )
            );

        user.markPhoneVerified();

        AuthenticationIdentity identity =
            AuthenticationIdentity.phone(
                user,
                phone
            );

        identities.save(identity);

        return new VerifiedUser(
            user.getId(),
            true
        );
    }

    public record OtpRequested(
        String challengeId,
        Duration expiresIn
    ) {
    }

    public record VerifiedUser(
        Long userId,
        boolean newAccount
    ) {
    }

    public enum VerificationFailure {
        INVALID,
        EXPIRED_OR_NOT_FOUND,
        ATTEMPTS_EXHAUSTED
    }

    public static final class OtpVerificationException
            extends RuntimeException {

        private final VerificationFailure failure;

        OtpVerificationException(
            VerificationFailure failure
        ) {
            this.failure = failure;
        }

        public VerificationFailure failure() {
            return failure;
        }
    }
}
