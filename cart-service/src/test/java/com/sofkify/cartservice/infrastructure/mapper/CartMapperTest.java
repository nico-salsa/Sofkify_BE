package com.sofkify.cartservice.infrastructure.mapper;

import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartItem;
import com.sofkify.cartservice.domain.model.CartStatus;
import com.sofkify.cartservice.infrastructure.adapters.out.persistence.CartItemJpaEntity;
import com.sofkify.cartservice.infrastructure.adapters.out.persistence.CartJpaEntity;
import com.sofkify.cartservice.infrastructure.adapters.out.persistence.CartStatusJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartMapper Tests")
class CartMapperTest {

    private CartMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CartMapper();
    }

    // --- toJpaEntity(Cart) ---

    @Test
    @DisplayName("Should map domain Cart to JPA entity")
    void shouldMapCartToJpaEntity() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Cart cart = new Cart(cartId, customerId, CartStatus.ACTIVE, now, now);
        cart.addItem(UUID.randomUUID(), "Product A", new BigDecimal("10.00"), 2);

        CartJpaEntity jpa = mapper.toJpaEntity(cart);

        assertNotNull(jpa);
        assertEquals(cartId, jpa.getId());
        assertEquals(customerId, jpa.getCustomerId());
        assertEquals(CartStatusJpa.ACTIVE, jpa.getStatus());
        assertEquals(now, jpa.getCreatedAt());
        assertEquals(1, jpa.getItems().size());
    }

    @Test
    @DisplayName("Should return null when mapping null Cart")
    void shouldReturnNullForNullCart() {
        assertNull(mapper.toJpaEntity((Cart) null));
    }

    @Test
    @DisplayName("Should map Cart with no items")
    void shouldMapCartWithNoItems() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID());
        CartJpaEntity jpa = mapper.toJpaEntity(cart);

        assertNotNull(jpa);
        assertTrue(jpa.getItems().isEmpty());
    }

    // --- toJpaEntity(CartItem, CartJpaEntity) ---

    @Test
    @DisplayName("Should map domain CartItem to JPA entity")
    void shouldMapCartItemToJpaEntity() {
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CartItem item = new CartItem(itemId, productId, "Test", new BigDecimal("5.50"), 3);

        CartJpaEntity parentJpa = new CartJpaEntity();
        parentJpa.setId(UUID.randomUUID());

        CartItemJpaEntity jpa = mapper.toJpaEntity(item, parentJpa);

        assertNotNull(jpa);
        assertEquals(itemId, jpa.getId());
        assertEquals(productId, jpa.getProductId());
        assertEquals("Test", jpa.getProductName());
        assertEquals(new BigDecimal("5.50"), jpa.getProductPrice());
        assertEquals(3, jpa.getQuantity());
        assertEquals(parentJpa, jpa.getCart());
    }

    @Test
    @DisplayName("Should return null when mapping null CartItem")
    void shouldReturnNullForNullCartItem() {
        assertNull(mapper.toJpaEntity((CartItem) null, new CartJpaEntity()));
    }

    // --- toDomainEntity(CartJpaEntity) ---

    @Test
    @DisplayName("Should map JPA entity to domain Cart")
    void shouldMapJpaToCart() {
        UUID cartId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CartJpaEntity jpa = new CartJpaEntity();
        jpa.setId(cartId);
        jpa.setCustomerId(customerId);
        jpa.setStatus(CartStatusJpa.CONFIRMED);
        jpa.setCreatedAt(now);
        jpa.setUpdatedAt(now);
        jpa.setItems(List.of());

        Cart cart = mapper.toDomainEntity(jpa);

        assertNotNull(cart);
        assertEquals(cartId, cart.getId());
        assertEquals(customerId, cart.getCustomerId());
        assertEquals(CartStatus.CONFIRMED, cart.getStatus());
    }

    @Test
    @DisplayName("Should map JPA entity with items to domain Cart")
    void shouldMapJpaWithItemsToCart() {
        UUID cartId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CartJpaEntity jpa = new CartJpaEntity();
        jpa.setId(cartId);
        jpa.setCustomerId(UUID.randomUUID());
        jpa.setStatus(CartStatusJpa.ACTIVE);
        jpa.setCreatedAt(now);
        jpa.setUpdatedAt(now);

        CartItemJpaEntity itemJpa = new CartItemJpaEntity();
        itemJpa.setId(UUID.randomUUID());
        itemJpa.setProductId(UUID.randomUUID());
        itemJpa.setProductName("Widget");
        itemJpa.setProductPrice(new BigDecimal("12.99"));
        itemJpa.setQuantity(4);
        itemJpa.setCreatedAt(now);
        itemJpa.setUpdatedAt(now);

        jpa.setItems(List.of(itemJpa));

        Cart cart = mapper.toDomainEntity(jpa);

        assertEquals(1, cart.getItems().size());
        assertEquals("Widget", cart.getItems().get(0).getProductName());
        assertEquals(4, cart.getItems().get(0).getQuantity());
    }

    @Test
    @DisplayName("Should return null when mapping null JPA entity")
    void shouldReturnNullForNullJpaEntity() {
        assertNull(mapper.toDomainEntity((CartJpaEntity) null));
    }

    @Test
    @DisplayName("Should handle JPA entity with null items list")
    void shouldHandleNullItemsList() {
        CartJpaEntity jpa = new CartJpaEntity();
        jpa.setId(UUID.randomUUID());
        jpa.setCustomerId(UUID.randomUUID());
        jpa.setStatus(CartStatusJpa.ACTIVE);
        jpa.setCreatedAt(LocalDateTime.now());
        jpa.setUpdatedAt(LocalDateTime.now());
        jpa.setItems(null);

        Cart cart = mapper.toDomainEntity(jpa);
        assertNotNull(cart);
        assertTrue(cart.getItems().isEmpty());
    }

    // --- toDomainEntity(CartItemJpaEntity) ---

    @Test
    @DisplayName("Should map JPA CartItem to domain CartItem")
    void shouldMapJpaItemToDomainItem() {
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CartItemJpaEntity jpa = new CartItemJpaEntity();
        jpa.setId(itemId);
        jpa.setProductId(productId);
        jpa.setProductName("Gadget");
        jpa.setProductPrice(new BigDecimal("29.99"));
        jpa.setQuantity(1);
        jpa.setCreatedAt(now);
        jpa.setUpdatedAt(now);

        CartItem item = mapper.toDomainEntity(jpa);

        assertNotNull(item);
        assertEquals(itemId, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals("Gadget", item.getProductName());
        assertEquals(new BigDecimal("29.99"), item.getProductPrice());
        assertEquals(1, item.getQuantity());
    }

    @Test
    @DisplayName("Should return null for null JPA CartItem")
    void shouldReturnNullForNullJpaCartItem() {
        assertNull(mapper.toDomainEntity((CartItemJpaEntity) null));
    }
}
