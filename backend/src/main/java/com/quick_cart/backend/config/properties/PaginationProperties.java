package com.quick_cart.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "quickcart.pagination")
public record PaginationProperties(
    int defaultSize,
    int maxSize
) {

    public PaginationProperties {
        if (defaultSize <= 0) {
            throw new IllegalArgumentException(
                "Default page size must be greater than zero"
            );
        }

        if (maxSize < defaultSize) {
            throw new IllegalArgumentException(
                "Maximum page size cannot be smaller than default page size"
            );
        }
    }
}
