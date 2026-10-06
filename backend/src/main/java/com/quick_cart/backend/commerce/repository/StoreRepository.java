package com.quick_cart.backend.commerce.repository;

import com.quick_cart.backend.commerce.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOpenTrueOrderByNameAsc();
}
