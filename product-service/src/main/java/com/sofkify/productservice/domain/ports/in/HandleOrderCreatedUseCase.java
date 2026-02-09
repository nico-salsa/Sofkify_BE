package com.sofkify.productservice.domain.ports.in;

import com.sofkify.productservice.application.dto.OrderCreatedEventDTO;
import java.util.UUID;

public interface HandleOrderCreatedUseCase {
    void handleOrderCreated(OrderCreatedEventDTO event);
}
