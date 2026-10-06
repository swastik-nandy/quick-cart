package com.quick_cart.backend.identity.api;

import com.quick_cart.backend.identity.otp.PhoneOtpAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class OtpExceptionHandler {

    @ExceptionHandler(
        PhoneOtpAuthService.OtpVerificationException.class
    )
    ResponseEntity<?> verificationFailed(
        PhoneOtpAuthService.OtpVerificationException exception
    ) {
        return switch (exception.failure()) {
            case INVALID ->
                ResponseEntity.badRequest().body(
                    Map.of("error", "INVALID_OTP")
                );

            case EXPIRED_OR_NOT_FOUND ->
                ResponseEntity.status(HttpStatus.GONE).body(
                    Map.of("error", "OTP_EXPIRED")
                );

            case ATTEMPTS_EXHAUSTED ->
                ResponseEntity.status(
                    HttpStatus.TOO_MANY_REQUESTS
                ).body(
                    Map.of("error", "OTP_ATTEMPTS_EXHAUSTED")
                );
        };
    }
}
