package com.sofkify.productservice.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEventDTO(
    @JsonProperty("orderId")
    UUID orderId,
    
    @JsonProperty("customerId")
    UUID customerId,
    
    @JsonProperty("cartId")
    UUID cartId,
    
    @JsonProperty("items")
    List<OrderItemEventDTO> items,
    
    @JsonProperty("totalAmount")
    BigDecimal totalAmount,
    
    @JsonProperty("createdAt")
    LocalDateTime createdAt
) {

    public record OrderItemEventDTO(
        @JsonProperty("productId")
        UUID productId,
        
        @JsonProperty("productName")
        String productName,
        
        @JsonProperty("quantity")
        int quantity,
        
        @JsonProperty("unitPrice")
        BigDecimal unitPrice,
        
        @JsonProperty("totalPrice")
        BigDecimal totalPrice
    ) {}
}
