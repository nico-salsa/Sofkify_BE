package com.sofkify.cartservice.domain.ports.in;

import com.sofkify.cartservice.application.dto.ConfirmCartRequest;
import com.sofkify.cartservice.application.dto.ConfirmCartResponse;

/**
 * Use case port para confirmar un carrito.
 * Define el contrato de entrada para la funcionalidad de confirmación de carrito.
 */
public interface ConfirmCartUseCase {
    
    /**
     * Ejecuta la confirmación de un carrito.
     * 
     * @param request datos necesarios para confirmar el carrito
     * @return respuesta con el resultado de la confirmación
     */
    ConfirmCartResponse execute(ConfirmCartRequest request);
}