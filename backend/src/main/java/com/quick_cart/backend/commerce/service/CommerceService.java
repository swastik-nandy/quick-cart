package com.quick_cart.backend.commerce.service;

import com.quick_cart.backend.commerce.domain.*;
import com.quick_cart.backend.commerce.repository.*;
import com.quick_cart.backend.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
public class CommerceService {
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("19.00");
    private final StoreRepository stores;
    private final ProductRepository products;
    private final CartItemRepository cartItems;
    private final DeliveryOrderRepository orders;
    private final UserRepository users;

    public CommerceService(StoreRepository stores, ProductRepository products, CartItemRepository cartItems, DeliveryOrderRepository orders, UserRepository users) {
        this.stores = stores;
        this.products = products;
        this.cartItems = cartItems;
        this.orders = orders;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> stores() {
        return stores.findByOpenTrueOrderByNameAsc().stream().map(this::store).toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> products(String category) {
        List<Product> list = category == null || category.isBlank()
            ? products.findByActiveTrueOrderByCategoryAscNameAsc()
            : products.findByCategoryIgnoreCaseAndActiveTrueOrderByNameAsc(category);
        return list.stream().map(this::product).toList();
    }

    @Transactional(readOnly = true)
    public List<String> categories() {
        return products.findByActiveTrueOrderByCategoryAscNameAsc().stream().map(Product::getCategory).distinct().toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> cart(Long userId) {
        return cartResponse(userId, cartItems.findByUserId(userId));
    }

    @Transactional
    public Map<String, Object> addToCart(Long userId, Long productId, int quantity) {
        var user = users.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        var product = products.findById(productId).orElseThrow(() -> new NoSuchElementException("Product not found"));
        if (!product.isActive() || product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Product unavailable");
        }
        CartItem item = cartItems.findByUserIdAndProductId(userId, productId).orElseGet(() -> new CartItem(user, product, quantity));
        item.setQuantity(quantity);
        cartItems.save(item);
        return cart(userId);
    }

    @Transactional
    public Map<String, Object> removeFromCart(Long userId, Long productId) {
        cartItems.deleteByUserIdAndProductId(userId, productId);
        return cart(userId);
    }

    @Transactional
    public Map<String, Object> checkout(Long userId, String deliveryAddress, String paymentMethod) {
        var user = users.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        var items = cartItems.findByUserId(userId);
        if (items.isEmpty()) throw new IllegalStateException("Cart is empty");
        var store = items.getFirst().getProduct().getStore();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            item.getProduct().reserve(item.getQuantity());
            subtotal = subtotal.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        DeliveryOrder order = new DeliveryOrder(user, store, deliveryAddress, subtotal, DELIVERY_FEE);
        for (CartItem item : items) order.addItem(item.getProduct(), item.getQuantity());
        DeliveryOrder saved = orders.save(order);
        cartItems.deleteByUserId(userId);
        return order(saved, paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> orders(Long userId) {
        return orders.findByUserIdOrderByPlacedAtDesc(userId).stream().map(order -> order(order, "CARD")).toList();
    }

    private Map<String, Object> cartResponse(Long userId, List<CartItem> items) {
        BigDecimal subtotal = items.stream().map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("userId", userId, "items", items.stream().map(this::cartItem).toList(), "subtotal", subtotal, "deliveryFee", items.isEmpty() ? BigDecimal.ZERO : DELIVERY_FEE, "total", items.isEmpty() ? BigDecimal.ZERO : subtotal.add(DELIVERY_FEE));
    }

    private Map<String, Object> store(Store s) {
        return Map.of("id", s.getId(), "name", s.getName(), "area", s.getArea(), "open", s.isOpen(), "etaMinutes", s.getEtaMinutes());
    }

    private Map<String, Object> product(Product p) {
        return Map.of("id", p.getId(), "store", store(p.getStore()), "name", p.getName(), "category", p.getCategory(), "imageUrl", p.getImageUrl(), "price", p.getPrice(), "stockQuantity", p.getStockQuantity(), "available", p.isActive() && p.getStockQuantity() > 0);
    }

    private Map<String, Object> cartItem(CartItem item) {
        return Map.of("product", product(item.getProduct()), "quantity", item.getQuantity(), "lineTotal", item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    }

    private Map<String, Object> order(DeliveryOrder order, String paymentMethod) {
        return Map.ofEntries(
            Map.entry("id", order.getId()),
            Map.entry("status", order.getStatus()),
            Map.entry("paymentStatus", "CONFIRMED"),
            Map.entry("paymentMethod", paymentMethod),
            Map.entry("store", store(order.getStore())),
            Map.entry("deliveryAddress", order.getDeliveryAddress()),
            Map.entry("subtotal", order.getSubtotal()),
            Map.entry("deliveryFee", order.getDeliveryFee()),
            Map.entry("total", order.getTotal()),
            Map.entry("etaMinutes", order.getEtaMinutes()),
            Map.entry("placedAt", order.getPlacedAt()),
            Map.entry("items", order.getItems().stream().map(i -> Map.of("name", i.getProductName(), "quantity", i.getQuantity(), "unitPrice", i.getUnitPrice(), "lineTotal", i.getLineTotal())).toList())
        );
    }
}
