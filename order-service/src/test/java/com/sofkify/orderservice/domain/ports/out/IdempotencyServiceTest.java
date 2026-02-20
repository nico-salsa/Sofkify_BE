package com.sofkify.orderservice.domain.ports.out;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IdempotencyService Interface - GREEN Phase")
class IdempotencyServiceTest {

    @Test
    @DisplayName("Should have isEventProcessed method")
    void shouldHaveIsEventProcessedMethod() {
        // Given
        UUID eventId = UUID.randomUUID();
        
        IdempotencyService service = new IdempotencyService() {
            @Override
            public boolean isEventProcessed(UUID eventId) {
                return false;
            }

            @Override
            public void markEventProcessed(UUID eventId, String eventType, UUID aggregateId) {
            }
        };
        
        // Then
        assertFalse(service.isEventProcessed(eventId));
    }

    @Test
    @DisplayName("Should have markEventProcessed method")
    void shouldHaveMarkEventProcessedMethod() {
        // Given
        UUID eventId = UUID.randomUUID();
        String eventType = "OrderCreatedEvent";
        UUID aggregateId = UUID.randomUUID();
        
        IdempotencyService service = new IdempotencyService() {
            @Override
            public boolean isEventProcessed(UUID eventId) {
                return false;
            }

            @Override
            public void markEventProcessed(UUID eventId, String eventType, UUID aggregateId) {
                // Implementation verified
            }
        };
        
        // Then
        assertDoesNotThrow(() -> service.markEventProcessed(eventId, eventType, aggregateId));
    }
}
