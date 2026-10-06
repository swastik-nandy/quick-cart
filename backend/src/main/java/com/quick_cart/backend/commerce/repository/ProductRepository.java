package com.quick_cart.backend.commerce.repository;

import com.quick_cart.backend.commerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrueOrderByCategoryAscNameAsc();
    List<Product> findByCategoryIgnoreCaseAndActiveTrueOrderByNameAsc(String category);
}
