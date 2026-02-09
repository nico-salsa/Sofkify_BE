package com.sofkify.orderservice.infrastructure.adapters.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofkify.orderservice.domain.event.OrderCreatedEvent;
import com.sofkify.orderservice.domain.ports.out.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQEventPublisherAdapter implements EventPublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String orderExchange;
    private final String orderCreatedRoutingKey;

    public RabbitMQEventPublisherAdapter(RabbitTemplate rabbitTemplate,
                                        ObjectMapper objectMapper,
                                        @Value("${rabbitmq.exchanges.order}") String orderExchange,
                                        @Value("${rabbitmq.routing-keys.order-created}") String orderCreatedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.orderExchange = orderExchange;
        this.orderCreatedRoutingKey = orderCreatedRoutingKey;
    }

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            logger.info("Publishing OrderCreatedEvent: {}", event.getOrderId());
            
            rabbitTemplate.convertAndSend(orderExchange, orderCreatedRoutingKey, eventJson);
            
            logger.info("OrderCreatedEvent published successfully for order: {}", event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Error serializing OrderCreatedEvent for order: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to serialize OrderCreatedEvent", e);
        } catch (Exception e) {
            logger.error("Error publishing OrderCreatedEvent for order: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to publish OrderCreatedEvent", e);
        }
    }
}
