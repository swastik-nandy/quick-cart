package com.quick_cart.backend.commerce.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(
    @NotNull Long userId,
    @NotBlank @Size(max = 500) String deliveryAddress,
    @NotBlank String paymentMethod
) {}
