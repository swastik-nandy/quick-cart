package com.quick_cart.backend.commerce.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products", schema = "commerce")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id")
    private Store store;
    @Column(nullable = false, length = 160)
    private String name;
    @Column(nullable = false, length = 80)
    private String category;
    @Column(nullable = false, length = 500)
    private String imageUrl;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private int stockQuantity;
    @Column(nullable = false)
    private boolean active;

    public void reserve(int quantity) {
        if (quantity <= 0 || stockQuantity < quantity) {
            throw new IllegalArgumentException("Product unavailable for requested quantity");
        }
        stockQuantity -= quantity;
    }

    public Long getId() { return id; }
    public Store getStore() { return store; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public boolean isActive() { return active; }
}
