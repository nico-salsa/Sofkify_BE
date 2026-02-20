package com.sofkify.cartservice.domain.model;

import com.sofkify.cartservice.domain.event.CartConfirmedEvent;
import com.sofkify.cartservice.domain.event.DomainEvent;
import com.sofkify.cartservice.domain.exception.CartAlreadyConfirmedException;
import com.sofkify.cartservice.domain.exception.InsufficientStockException;
import com.sofkify.cartservice.domain.ports.out.StockValidationPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Cart {
    private final UUID id;
    private final UUID customerId;
    private CartStatus status;
    private final List<CartItem> items;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Cart(UUID id, UUID customerId) {
        this.id = Objects.requireNonNull(id, "Cart ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.status = CartStatus.ACTIVE;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(UUID productId, String productName, BigDecimal productPrice, int quantity) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(productPrice, "Product price cannot be null");
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Buscar si el producto ya existe en el carrito
        Optional<CartItem> existingItem = findItemByProductId(productId);
        
        if (existingItem.isPresent()) {
            // Si existe, actualizar cantidad
            CartItem item = existingItem.get();
            item.updateQuantity(item.getQuantity() + quantity);
        } else {
            // Si no existe, crear nuevo item
            CartItem newItem = new CartItem(
                UUID.randomUUID(),
                productId,
                productName,
                productPrice,
                quantity
            );
            items.add(newItem);
        }
        
        this.updatedAt = LocalDateTime.now();
    }

    public void addExistingItem(CartItem cartItem) {
        Objects.requireNonNull(cartItem, "Cart item cannot be null");
        items.add(cartItem);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeItem(CartItem cartItem) {
        Objects.requireNonNull(cartItem, "Cart item cannot be null");
        items.remove(cartItem);
        this.updatedAt = LocalDateTime.now();
    }

    private Optional<CartItem> findItemByProductId(UUID productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();
    }
    
    /**
     * Confirms the cart after validating stock availability for all items.
     * Transitions cart status from ACTIVE to CONFIRMED.
     * 
     * @param stockValidator port to validate stock availability
     * @throws CartAlreadyConfirmedException if cart is already confirmed
     * @throws InsufficientStockException if any item has insufficient stock
     */
    public void confirm(StockValidationPort stockValidator) {
        Objects.requireNonNull(stockValidator, "Stock validator cannot be null");
        
        // Validate current status
        if (this.status == CartStatus.CONFIRMED) {
            throw new CartAlreadyConfirmedException(this.id);
        }
        
        // Validate stock for all items
        for (CartItem item : this.items) {
            boolean stockAvailable = stockValidator.isStockAvailable(item.getProductId(), item.getQuantity());
            if (!stockAvailable) {
                throw new InsufficientStockException(item.getProductId());
            }
        }
        
        // Transition to confirmed status
        this.status = CartStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
        
        // Publish domain event
        BigDecimal totalAmount = calculateTotal();
        this.domainEvents.add(new CartConfirmedEvent(this.id, this.customerId, this.items, totalAmount));
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public CartStatus getStatus() { return status; }
    public List<CartItem> getItems() { return Collections.unmodifiableList(items); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
