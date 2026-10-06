package com.quick_cart.backend.commerce.repository;

import com.quick_cart.backend.commerce.domain.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
    List<DeliveryOrder> findByUserIdOrderByPlacedAtDesc(Long userId);
}
