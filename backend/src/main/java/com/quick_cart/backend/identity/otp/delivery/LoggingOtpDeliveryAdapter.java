package com.quick_cart.backend.identity.otp.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConditionalOnProperty(
    prefix = "quickcart.auth.otp",
    name = "delivery-mode",
    havingValue = "log"
)
final class LoggingOtpDeliveryAdapter
        implements OtpDeliveryPort {

    private static final Logger log =
        LoggerFactory.getLogger(
            LoggingOtpDeliveryAdapter.class
        );

    @Override
    public void send(
        String phoneE164,
        String otp,
        Duration expiresIn
    ) {
        log.warn(
            "LOCAL OTP delivery phone={} otp={} expiresIn={}s",
            phoneE164,
            otp,
            expiresIn.toSeconds()
        );
    }
}
