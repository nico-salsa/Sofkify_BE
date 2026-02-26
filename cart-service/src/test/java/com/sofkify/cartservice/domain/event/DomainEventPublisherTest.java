package com.sofkify.cartservice.domain.event;

import com.sofkify.cartservice.domain.ports.out.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

@DisplayName("DomainEventPublisher Tests - RED Phase")
class DomainEventPublisherTest {

    @Test
    @DisplayName("Should pass - DomainEventPublisher interface exists")
    void shouldPassDomainEventPublisherInterfaceExists() {
        // This test validates the DomainEventPublisher interface exists
        DomainEventPublisher publisher = event -> {
            // Mock implementation for testing
            assertNotNull(event);
        };
        
        assertNotNull(publisher);
    }

    @Test 
    @DisplayName("Should pass - publish method works with domain events")
    void shouldPassPublishMethodWorksWithDomainEvents() {
        // Mock implementation to test the interface contract  
        DomainEventPublisher publisher = new DomainEventPublisher() {
            @Override
            public void publish(DomainEvent event) {
                assertNotNull(event);
                assertNotNull(event.getEventId());
                assertNotNull(event.getOccurredOn()); 
                assertNotNull(event.getEventType());
            }
        };
        
        DomainEvent event = createMockDomainEvent();
        
        // This should work with the interface
        assertDoesNotThrow(() -> publisher.publish(event));
    }

    @Test
    @DisplayName("Should pass - multiple events can be published in sequence")
    void shouldPassMultipleEventsCanBePublishedInSequence() {
        // Test sequential event publishing
        DomainEventPublisher publisher = event -> {
            // Mock implementation that just validates the event
            assertNotNull(event);
        };
        
        DomainEvent event1 = createMockDomainEvent();
        DomainEvent event2 = createMockDomainEvent();
        
        // Should be able to publish multiple events in sequence
        assertDoesNotThrow(() -> {
            publisher.publish(event1);
            publisher.publish(event2);
        });
    }

    private DomainEvent createMockDomainEvent() {
        return new DomainEvent() {
            @Override
            public UUID getEventId() { 
                return UUID.randomUUID(); 
            }
            
            @Override
            public java.time.LocalDateTime getOccurredOn() { 
                return java.time.LocalDateTime.now(); 
            }
            
            @Override
            public String getEventType() { 
                return "MockEvent"; 
            }
        };
    }
}