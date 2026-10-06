package com.quick_cart.backend.identity.auth;

import com.quick_cart.backend.config.properties.JwtProperties;
import com.quick_cart.backend.identity.domain.User;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    public String issue(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.expiry());
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64Url(
            "{\"sub\":\"" + user.getId() + "\","
                + "\"email\":\"" + escape(user.getEmail()) + "\","
                + "\"name\":\"" + escape(user.getDisplayName()) + "\","
                + "\"iat\":" + now.getEpochSecond() + ","
                + "\"exp\":" + expiresAt.getEpochSecond() + "}"
        );
        String unsigned = header + "." + payload;
        return unsigned + "." + sign(unsigned);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not sign JWT", ex);
        }
    }

    private static String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
