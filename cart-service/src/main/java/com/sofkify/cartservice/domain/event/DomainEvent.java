package com.sofkify.cartservice.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Interfaz base para todos los eventos de dominio del carrito.
 * Representa hechos que ya ocurrieron en el dominio de carritos.
 */
public interface DomainEvent {
    
    /**
     * @return Identificador único del evento
     */
    UUID getEventId();
    
    /**
     * @return Momento en que ocurrió el evento
     */
    LocalDateTime getOccurredOn();
    
    /**
     * @return Tipo del evento para identificación
     */
    String getEventType();
}