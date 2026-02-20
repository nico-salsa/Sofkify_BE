package com.sofkify.orderservice.domain.model;

import com.sofkify.orderservice.domain.valueobject.Money;
import com.sofkify.orderservice.domain.valueobject.Quantity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class OrderItem {
    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final Money unitPrice;
    private final Quantity quantity;
    private final Money subtotal;
    private final LocalDateTime createdAt;

    public OrderItem(UUID id, UUID productId, String productName, Money unitPrice, Quantity quantity) {
        this.id = Objects.requireNonNull(id, "Order item ID cannot be null");
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product name cannot be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
        
        if (productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        
        this.subtotal = unitPrice.multiply(quantity.value());
        this.createdAt = LocalDateTime.now();
    }

    // Constructor legacy para compatibilidad con BigDecimal y int
    public OrderItem(UUID id, UUID productId, String productName, BigDecimal productPrice, int quantity) {
        this(id, productId, productName, new Money(productPrice), new Quantity(quantity));
    }

    // Factory method para crear desde CartItem
    public static OrderItem fromCartItem(UUID orderItemId, Object cartItem) {
        // TODO: Implementar cuando CartItem esté disponible
        // Por ahora, devolver un OrderItem de prueba
        return new OrderItem(
            orderItemId,
            UUID.randomUUID(), // productId temporal
            "Test Product",    // productName temporal
            new Money(BigDecimal.TEN), // unitPrice temporal
            new Quantity(1)    // quantity temporal
        );
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Money getUnitPrice() { return unitPrice; }
    public Quantity getQuantity() { return quantity; }
    public Money getSubtotal() { return subtotal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Getters legacy para compatibilidad
    public BigDecimal getProductPrice() { return unitPrice.amount(); }
    public int getQuantityValue() { return quantity.value(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
