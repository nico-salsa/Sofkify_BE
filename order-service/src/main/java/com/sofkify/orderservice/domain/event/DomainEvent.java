package com.sofkify.orderservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interfaz base para todos los eventos de dominio.
 * Representa hechos que ya ocurrieron en el dominio de órdenes.
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