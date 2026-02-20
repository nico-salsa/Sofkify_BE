package com.sofkify.orderservice.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity tracking processed domain events for idempotency.
 * Prevents duplicate processing of events (e.g., from RabbitMQ retries).
 */
public class ProcessedEvent {
    
    private final UUID eventId;
    private final String eventType;
    private final UUID aggregateId;
    private final LocalDateTime processedAt;
    
    public ProcessedEvent(UUID eventId, String eventType, UUID aggregateId) {
        this.eventId = Objects.requireNonNull(eventId, "Event ID cannot be null");
        this.eventType = Objects.requireNonNull(eventType, "Event type cannot be null");
        this.aggregateId = Objects.requireNonNull(aggregateId, "Aggregate ID cannot be null");
        this.processedAt = LocalDateTime.now();
    }
    
    public UUID getEventId() {
        return eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public UUID getAggregateId() {
        return aggregateId;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessedEvent that = (ProcessedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
    
    @Override
    public String toString() {
        return "ProcessedEvent{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", aggregateId=" + aggregateId +
                ", processedAt=" + processedAt +
                '}';
    }
}
