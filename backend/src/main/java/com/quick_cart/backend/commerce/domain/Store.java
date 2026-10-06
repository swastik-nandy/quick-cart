package com.quick_cart.backend.commerce.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "stores", schema = "commerce")
public class Store {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 120)
    private String area;
    @Column(nullable = false)
    private boolean open;
    @Column(name = "eta_minutes", nullable = false)
    private int etaMinutes;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getArea() { return area; }
    public boolean isOpen() { return open; }
    public int getEtaMinutes() { return etaMinutes; }
}
