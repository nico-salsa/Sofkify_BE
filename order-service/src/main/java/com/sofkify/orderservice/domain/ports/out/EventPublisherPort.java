package com.sofkify.orderservice.domain.ports.out;

import com.sofkify.orderservice.domain.event.OrderCreatedEvent;

public interface EventPublisherPort {
    void publish(OrderCreatedEvent event);
}
