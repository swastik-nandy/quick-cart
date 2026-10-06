package com.quick_cart.backend.commerce.domain;

import com.quick_cart.backend.identity.domain.User;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items", schema = "commerce", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id")
    private Product product;
    @Column(nullable = false)
    private int quantity;

    protected CartItem() {}
    public CartItem(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        setQuantity(quantity);
    }
    public void setQuantity(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = quantity;
    }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
}
