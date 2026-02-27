package com.sofkify.productservice.infrastructure.messaging.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofkify.productservice.application.dto.OrderCreatedEventDTO;
import com.sofkify.productservice.domain.ports.in.HandleOrderCreatedUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RabbitMQOrderCreatedConsumer Tests")
class RabbitMQOrderCreatedConsumerTest {

    private ObjectMapper objectMapper;
    private HandleOrderCreatedUseCase handleOrderCreatedUseCase;
    private RabbitMQOrderCreatedConsumer consumer;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        handleOrderCreatedUseCase = mock(HandleOrderCreatedUseCase.class);
        consumer = new RabbitMQOrderCreatedConsumer(objectMapper, handleOrderCreatedUseCase);
    }

    @Test
    @DisplayName("Should parse valid message and delegate to use case")
    void shouldProcessValidMessage() throws Exception {
        String json = "{\"orderId\":\"123\"}";
        OrderCreatedEventDTO dto = new OrderCreatedEventDTO(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            List.of(), BigDecimal.TEN, LocalDateTime.now()
        );

        when(objectMapper.readValue(json, OrderCreatedEventDTO.class)).thenReturn(dto);

        consumer.handleOrderCreated(json);

        verify(handleOrderCreatedUseCase).handleOrderCreated(dto);
    }

    @Test
    @DisplayName("Should throw RuntimeException on JsonProcessingException")
    void shouldThrowOnParsingError() throws Exception {
        String badJson = "invalid";

        when(objectMapper.readValue(badJson, OrderCreatedEventDTO.class))
            .thenThrow(new JsonProcessingException("parse error") {});

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> consumer.handleOrderCreated(badJson));
        assertTrue(ex.getMessage().contains("Failed to parse"));
    }

    @Test
    @DisplayName("Should throw RuntimeException when use case fails")
    void shouldThrowOnUseCaseError() throws Exception {
        String json = "{\"orderId\":\"456\"}";
        OrderCreatedEventDTO dto = new OrderCreatedEventDTO(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            List.of(), BigDecimal.ONE, LocalDateTime.now()
        );

        when(objectMapper.readValue(json, OrderCreatedEventDTO.class)).thenReturn(dto);
        doThrow(new IllegalStateException("use case error"))
            .when(handleOrderCreatedUseCase).handleOrderCreated(dto);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> consumer.handleOrderCreated(json));
        assertTrue(ex.getMessage().contains("Failed to process"));
    }
}
