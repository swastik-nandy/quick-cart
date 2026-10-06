package com.quick_cart.backend.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "authentication_identities", schema = "identity")
public class AuthenticationIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 30)
    private AuthenticationProvider provider;

    @Column(name = "provider_subject", nullable = false, length = 255)
    private String providerSubject;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_authenticated_at")
    private Instant lastAuthenticatedAt;

    protected AuthenticationIdentity() {
    }


    public static AuthenticationIdentity emailPassword(
        User user,
        String email
    ) {
        AuthenticationIdentity identity =
            new AuthenticationIdentity();

        identity.user = user;
        identity.provider = AuthenticationProvider.EMAIL_PASSWORD;
        identity.providerSubject = email.toLowerCase();
        identity.lastAuthenticatedAt = Instant.now();

        return identity;
    }

    public void recordAuthentication() {
        this.lastAuthenticatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public AuthenticationProvider getProvider() {
        return provider;
    }

    public String getProviderSubject() {
        return providerSubject;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastAuthenticatedAt() {
        return lastAuthenticatedAt;
    }
}
