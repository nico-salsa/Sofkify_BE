package com.sofkify.orderservice.domain.ports.out;

import com.sofkify.orderservice.domain.model.Order;
import java.util.Optional;
import java.util.UUID;

/**
 * Port para persistencia de órdenes.
 * Abstrae el acceso a datos de órdenes.
 */
public interface OrderRepository {
    
    /**
     * Busca una orden por su ID.
     * 
     * @param orderId el identificador de la orden
     * @return la orden si existe, Optional.empty() en caso contrario
     */
    Optional<Order> findById(UUID orderId);
    
    /**
     * Guarda una orden.
     * 
     * @param order la orden a guardar
     * @return la orden guardada
     */
    Order save(Order order);
    
    /**
     * Busca órdenes por customer ID.
     * 
     * @param customerId el identificador del customer
     * @return lista de órdenes del customer
     */
    java.util.List<Order> findByCustomerId(UUID customerId);
}