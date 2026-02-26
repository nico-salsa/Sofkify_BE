package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.application.dto.ConfirmCartRequest;
import com.sofkify.cartservice.application.dto.ConfirmCartResponse;
import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.ports.in.ConfirmCartUseCase;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import com.sofkify.cartservice.domain.ports.out.ProductServicePort;
import com.sofkify.cartservice.domain.ports.out.StockValidationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Servicio de aplicacion para confirmar carritos.
 */
@Service
@Transactional
public class ConfirmCartService implements ConfirmCartUseCase {

    private final CartRepositoryPort cartRepository;
    private final ProductServicePort productServicePort;

    public ConfirmCartService(CartRepositoryPort cartRepository,
                              ProductServicePort productServicePort) {
        this.cartRepository = Objects.requireNonNull(cartRepository, "Cart repository cannot be null");
        this.productServicePort = Objects.requireNonNull(productServicePort, "Product service port cannot be null");
    }

    @Override
    public ConfirmCartResponse execute(ConfirmCartRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");

        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new CartException("Cart not found: " + request.getCartId()));

        if (!cart.getCustomerId().equals(request.getCustomerId())) {
            throw new CartException("Cart does not belong to customer");
        }

        cart.confirm(new ProductStockValidationPort(productServicePort));
        Cart savedCart = cartRepository.save(cart);
        savedCart.clearDomainEvents();

        return new ConfirmCartResponse(savedCart.getId(), true, "Cart confirmed successfully");
    }

    /**
     * Adapter minimo para validar stock con el ProductServicePort existente.
     */
    private static class ProductStockValidationPort implements StockValidationPort {

        private final ProductServicePort productServicePort;

        private ProductStockValidationPort(ProductServicePort productServicePort) {
            this.productServicePort = productServicePort;
        }

        @Override
        public boolean isStockAvailable(UUID productId, int requestedQuantity) {
            return productServicePort.validateStock(productId, requestedQuantity);
        }

        @Override
        public int getAvailableStock(UUID productId) {
            return productServicePort.getProduct(productId).stock();
        }

        @Override
        public boolean reserveStock(UUID productId, int quantity) {
            return productServicePort.validateStock(productId, quantity);
        }

        @Override
        public void returnStock(UUID productId, int quantity) {
            // No-op: no existe endpoint compensatorio en el contrato actual.
        }
    }
}

