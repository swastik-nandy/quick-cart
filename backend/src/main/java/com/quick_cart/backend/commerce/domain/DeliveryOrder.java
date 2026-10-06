package com.quick_cart.backend.commerce.domain;

import com.quick_cart.backend.identity.domain.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", schema = "commerce")
public class DeliveryOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "store_id")
    private Store store;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PAYMENT_CONFIRMED;
    @Column(nullable = false, length = 500)
    private String deliveryAddress;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    @Column(nullable = false)
    private int etaMinutes;
    @Column(nullable = false)
    private Instant placedAt = Instant.now();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected DeliveryOrder() {}
    public DeliveryOrder(User user, Store store, String deliveryAddress, BigDecimal subtotal, BigDecimal deliveryFee) {
        this.user = user;
        this.store = store;
        this.deliveryAddress = deliveryAddress;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.total = subtotal.add(deliveryFee);
        this.etaMinutes = store.getEtaMinutes();
    }
    public void addItem(Product product, int quantity) {
        items.add(new OrderItem(this, product, quantity));
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Store getStore() { return store; }
    public OrderStatus getStatus() { return status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public BigDecimal getTotal() { return total; }
    public int getEtaMinutes() { return etaMinutes; }
    public Instant getPlacedAt() { return placedAt; }
    public List<OrderItem> getItems() { return List.copyOf(items); }
}
