package com.quick_cart.backend.identity.otp.delivery;

import java.time.Duration;

public interface OtpDeliveryPort {

    void send(
        String phoneE164,
        String otp,
        Duration expiresIn
    );
}
