package com.sofkify.orderservice.domain.ports.in;

import com.sofkify.orderservice.application.dto.CreateOrderRequest;
import com.sofkify.orderservice.application.dto.CreateOrderResponse;

/**
 * Use case port para crear una orden.
 * Define el contrato de entrada para la funcionalidad de creación de órdenes.
 */
public interface CreateOrderUseCase {
    
    /**
     * Ejecuta la creación de una orden.
     * 
     * @param request datos necesarios para crear la orden
     * @return respuesta con el resultado de la creación
     */
    CreateOrderResponse execute(CreateOrderRequest request);
}