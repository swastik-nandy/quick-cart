package com.quick_cart.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class QuickCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickCartApplication.class, args);
    }
}
