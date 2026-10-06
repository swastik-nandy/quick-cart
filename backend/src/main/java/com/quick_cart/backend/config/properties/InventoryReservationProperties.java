package com.quick_cart.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "quickcart.inventory.reservation")
public record InventoryReservationProperties(
    Duration expiry
) {

    public InventoryReservationProperties {
        if (expiry == null || expiry.isZero() || expiry.isNegative()) {
            throw new IllegalArgumentException(
                "Inventory reservation expiry must be greater than zero"
            );
        }
    }
}
