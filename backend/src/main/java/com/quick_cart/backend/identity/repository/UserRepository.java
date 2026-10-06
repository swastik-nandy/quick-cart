package com.quick_cart.backend.identity.repository;

import com.quick_cart.backend.identity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByPhoneE164(String phoneE164);
}
