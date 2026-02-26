package com.sofkify.orderservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcessedEvent Entity - RED Phase")
class ProcessedEventTest {

    @Test
    @DisplayName("Should fail - ProcessedEvent entity does not exist")
    void shouldFailProcessedEventDoesNotExist() {
        // Given
        UUID eventId = UUID.randomUUID();
        String eventType = "OrderCreatedEvent";
        UUID aggregateId = UUID.randomUUID();
        
        // When - This should fail because ProcessedEvent doesn't exist yet
        ProcessedEvent event = new ProcessedEvent(eventId, eventType, aggregateId);
        
        // Then
        assertNotNull(event);
        assertEquals(eventId, event.getEventId());
        assertEquals(eventType, event.getEventType());
        assertEquals(aggregateId, event.getAggregateId());
        assertNotNull(event.getProcessedAt());
    }

    @Test
    @DisplayName("Should fail - cannot create ProcessedEvent with null eventId")
    void shouldFailWithNullEventId() {
        // Given
        UUID eventId = null;
        String eventType = "OrderCreatedEvent";
        UUID aggregateId = UUID.randomUUID();
        
        // When / Then
        assertThrows(NullPointerException.class, 
            () -> new ProcessedEvent(eventId, eventType, aggregateId));
    }

    @Test
    @DisplayName("Should fail - ProcessedEvent should track processing timestamp")
    void shouldTrackProcessingTimestamp() {
        // Given
        UUID eventId = UUID.randomUUID();
        ProcessedEvent event = new ProcessedEvent(eventId, "OrderCreatedEvent", UUID.randomUUID());
        
        // Then
        assertNotNull(event.getProcessedAt());
        assertTrue(event.getProcessedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
