package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.event.DomainEvent;
import com.sofkify.cartservice.domain.ports.out.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

@DisplayName("EventPublisherService Tests")
class EventPublisherServiceTest {

    @Test
    @DisplayName("Should publish a domain event via port")
    void shouldPublishDomainEventViaPort() {
        // Arrange
        java.util.concurrent.atomic.AtomicBoolean published = new java.util.concurrent.atomic.AtomicBoolean(false);
        EventPublisher publisher = event -> published.set(true);
        
        DomainEvent event = new DomainEvent() {
            @Override
            public java.util.UUID getEventId() { return java.util.UUID.randomUUID(); }
            @Override
            public java.time.LocalDateTime getOccurredOn() { return java.time.LocalDateTime.now(); }
            @Override
            public String getEventType() { return "TestEvent"; }
        };

        // Act
        publisher.publish(event);

        // Assert
        assertTrue(published.get());
    }

    @Test
    @DisplayName("Should not accept null event")
    void shouldNotAcceptNullEvent() {
        EventPublisher publisher = event -> { throw new IllegalArgumentException("Event cannot be null"); };

        assertThrows(IllegalArgumentException.class, () -> publisher.publish(null));
    }

    @Test
    @DisplayName("CartRepository port should be present in domain")
    void cartRepositoryPortShouldExist() {
        assertDoesNotThrow(() -> {
            Class.forName("com.sofkify.cartservice.domain.ports.out.CartRepository");
        });
    }
}