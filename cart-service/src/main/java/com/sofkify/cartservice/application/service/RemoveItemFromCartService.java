package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartItem;
import com.sofkify.cartservice.domain.ports.in.RemoveItemFromCartUseCase;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RemoveItemFromCartService implements RemoveItemFromCartUseCase {

    private final CartRepositoryPort cartRepository;

    public RemoveItemFromCartService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Removes a specific cart item from the current customer's cart.
     *
     * @param customerId customer identifier used to locate the cart
     * @param cartItemId cart item identifier to remove
     * @return updated persisted cart
     * @throws CartException when the cart or the item cannot be found
     */
    @Override
    public Cart removeItemFromCart(UUID customerId, UUID cartItemId) {
        // Obtener el carrito del cliente
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartException("Cart not found for customer: " + customerId));

        // Buscar el item específico por su ID
        Optional<CartItem> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        if (itemToRemove.isEmpty()) {
            throw new CartException("Cart item not found: " + cartItemId);
        }

        // Eliminar el item del carrito
        cart.removeItem(itemToRemove.get());

        // Guardar el carrito actualizado
        return cartRepository.save(cart);
    }
}
