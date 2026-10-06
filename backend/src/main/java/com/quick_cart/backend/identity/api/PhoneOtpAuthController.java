package com.quick_cart.backend.identity.api;

import com.quick_cart.backend.identity.otp.PhoneOtpAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/otp")
public class PhoneOtpAuthController {

    private final PhoneOtpAuthService auth;

    public PhoneOtpAuthController(
        PhoneOtpAuthService auth
    ) {
        this.auth = auth;
    }

    @PostMapping("/request")
    public ResponseEntity<?> request(
        @Valid @RequestBody RequestOtpRequest request
    ) {
        var result = auth.request(
            request.phoneE164()
        );

        return ResponseEntity.accepted().body(
            Map.of(
                "challengeId",
                result.challengeId(),
                "expiresInSeconds",
                result.expiresIn().toSeconds()
            )
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
        @Valid @RequestBody VerifyOtpRequest request
    ) {
        var result = auth.verify(
            request.phoneE164(),
            request.challengeId(),
            request.otp()
        );

        return ResponseEntity.ok(
            Map.of(
                "status",
                "VERIFIED",
                "userId",
                result.userId(),
                "newAccount",
                result.newAccount()
            )
        );
    }
}
