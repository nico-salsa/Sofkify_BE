package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.application.dto.ConfirmCartRequest;
import com.sofkify.cartservice.application.dto.ConfirmCartResponse;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.ports.in.ConfirmCartUseCase;
import com.sofkify.cartservice.domain.ports.out.CartRepository;
import com.sofkify.cartservice.domain.ports.out.EventPublisher;
import com.sofkify.cartservice.domain.ports.out.StockValidationPort;

import java.util.Objects;

/**
 * Servicio de aplicación para confirmar carritos.
 * Implementa el use case de confirmación de carrito.
 */
public class ConfirmCartService implements ConfirmCartUseCase {
    
    private final CartRepository cartRepository;
    private final StockValidationPort stockValidationPort;
    private final EventPublisher eventPublisher;
    
    public ConfirmCartService(CartRepository cartRepository, 
                             StockValidationPort stockValidationPort,
                             EventPublisher eventPublisher) {
        this.cartRepository = Objects.requireNonNull(cartRepository, "Cart repository cannot be null");
        this.stockValidationPort = Objects.requireNonNull(stockValidationPort, "Stock validation port cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
    }
    
    @Override
    public ConfirmCartResponse execute(ConfirmCartRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        try {
            // Buscar el carrito
            Cart cart = cartRepository.findById(request.getCartId())
                    .orElseThrow(() -> new RuntimeException("Cart not found: " + request.getCartId()));
            
            // Validar que el carrito pertenezca al customer
            if (!cart.getCustomerId().equals(request.getCustomerId())) {
                return new ConfirmCartResponse(request.getCartId(), false, "Cart does not belong to customer");
            }
            
            // Confirmar el carrito (esto valida stock y cambia estado)
            cart.confirm(stockValidationPort);
            
            // Guardar el carrito confirmado
            Cart savedCart = cartRepository.save(cart);
            
            // Publicar eventos de dominio
            savedCart.getDomainEvents().forEach(eventPublisher::publish);
            savedCart.clearDomainEvents();
            
            return new ConfirmCartResponse(savedCart.getId(), true, "Cart confirmed successfully");
            
        } catch (Exception e) {
            return new ConfirmCartResponse(request.getCartId(), false, "Error confirming cart: " + e.getMessage());
        }
    }
}
