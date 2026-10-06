package com.quick_cart.backend.identity.otp.delivery;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConditionalOnProperty(
    prefix = "quickcart.auth.otp",
    name = "delivery-mode",
    havingValue = "disabled",
    matchIfMissing = true
)
final class DisabledOtpDeliveryAdapter
        implements OtpDeliveryPort {

    @Override
    public void send(
        String phoneE164,
        String otp,
        Duration expiresIn
    ) {
        throw new IllegalStateException(
            "OTP delivery provider is not configured"
        );
    }
}
