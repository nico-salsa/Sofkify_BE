package com.sofkify.productservice.infrastructure.web.mapper;

import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.web.dto.request.CreateProductRequest;
import com.sofkify.productservice.infrastructure.web.dto.response.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductDtoMapper Tests")
class ProductDtoMapperTest {

    private ProductDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductDtoMapper();
    }

    @Test
    @DisplayName("Should map Product to ProductResponse")
    void shouldMapToDto() {
        Product product = Product.reconstitute(UUID.randomUUID(), "Laptop", "Desc", "SKU-001",
            BigDecimal.valueOf(999.99), 10, ProductStatus.ACTIVE);

        ProductResponse dto = mapper.toDto(product);

        assertEquals(product.getId(), dto.productId());
        assertEquals("Laptop", dto.name());
        assertEquals("Desc", dto.description());
        assertEquals("SKU-001", dto.sku());
        assertEquals(BigDecimal.valueOf(999.99), dto.price());
        assertEquals(10, dto.stock());
        assertEquals(ProductStatus.ACTIVE, dto.status());
    }

    @Test
    @DisplayName("Should map CreateProductRequest to CreateProductCommand")
    void shouldMapToCommand() {
        CreateProductRequest request = new CreateProductRequest("P", "D", "SKU", BigDecimal.TEN, 5);

        CreateProductCommand cmd = mapper.toCommand(request);

        assertEquals("P", cmd.name());
        assertEquals("D", cmd.description());
        assertEquals("SKU", cmd.sku());
        assertEquals(BigDecimal.TEN, cmd.price());
        assertEquals(5, cmd.stock());
    }
}
