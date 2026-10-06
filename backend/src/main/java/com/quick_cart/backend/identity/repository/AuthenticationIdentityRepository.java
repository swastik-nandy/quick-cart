package com.quick_cart.backend.identity.repository;

import com.quick_cart.backend.identity.domain.AuthenticationIdentity;
import com.quick_cart.backend.identity.domain.AuthenticationProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationIdentityRepository
        extends JpaRepository<AuthenticationIdentity, Long> {

    Optional<AuthenticationIdentity> findByProviderAndProviderSubject(
        AuthenticationProvider provider,
        String providerSubject
    );
}
