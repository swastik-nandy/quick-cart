package com.quick_cart.backend.identity.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RequestOtpRequest(

    @NotBlank
    @Pattern(regexp = "^\\+[1-9][0-9]{7,14}$")
    String phoneE164

) {
}
