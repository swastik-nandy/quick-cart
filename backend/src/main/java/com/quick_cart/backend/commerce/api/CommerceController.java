package com.quick_cart.backend.commerce.api;

import com.quick_cart.backend.commerce.service.CommerceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CommerceController {
    private final CommerceService commerce;

    public CommerceController(CommerceService commerce) {
        this.commerce = commerce;
    }

    @GetMapping("/stores")
    public List<Map<String, Object>> stores() {
        return commerce.stores();
    }

    @GetMapping("/products")
    public List<Map<String, Object>> products(@RequestParam(required = false) String category) {
        return commerce.products(category);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return commerce.categories();
    }

    @GetMapping("/users/{userId}/cart")
    public Map<String, Object> cart(@PathVariable Long userId) {
        return commerce.cart(userId);
    }

    @PutMapping("/users/{userId}/cart/items")
    public Map<String, Object> addToCart(@PathVariable Long userId, @Valid @RequestBody AddCartItemRequest request) {
        return commerce.addToCart(userId, request.productId(), request.quantity());
    }

    @DeleteMapping("/users/{userId}/cart/items/{productId}")
    public Map<String, Object> removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        return commerce.removeFromCart(userId, productId);
    }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(@Valid @RequestBody CheckoutRequest request) {
        return commerce.checkout(request.userId(), request.deliveryAddress(), request.paymentMethod());
    }

    @GetMapping("/users/{userId}/orders")
    public List<Map<String, Object>> orders(@PathVariable Long userId) {
        return commerce.orders(userId);
    }
}
