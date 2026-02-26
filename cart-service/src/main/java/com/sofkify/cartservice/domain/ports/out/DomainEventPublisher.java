package com.sofkify.cartservice.domain.ports.out;

import com.sofkify.cartservice.domain.event.DomainEvent;

/**
 * Port for publishing domain events to the infrastructure layer.
 * This is a minimal interface to make RED tests compile.
 */
public interface DomainEventPublisher {
    
    /**
     * Publishes a single domain event.
     * 
     * @param event The domain event to publish
     */
    void publish(DomainEvent event);
}