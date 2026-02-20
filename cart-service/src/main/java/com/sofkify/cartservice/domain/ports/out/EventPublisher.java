package com.sofkify.cartservice.domain.ports.out;

import com.sofkify.cartservice.domain.event.DomainEvent;

/**
 * Port para publicar eventos de dominio.
 * Abstrae la comunicación con sistemas de messaging (RabbitMQ, etc.).
 */
public interface EventPublisher {
    
    /**
     * Publica un evento de dominio.
     * 
     * @param event el evento a publicar
     */
    void publish(DomainEvent event);
}