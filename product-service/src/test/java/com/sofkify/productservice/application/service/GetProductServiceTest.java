package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.exception.ProductNotFoundException;
import com.sofkify.productservice.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GetProductService Tests")
class GetProductServiceTest {

    @Mock private ProductPersistencePort persistencePort;
    private GetProductService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GetProductService(persistencePort);
    }

    private Product testProduct() {
        return Product.reconstitute(UUID.randomUUID(), "P", "D", "SKU", BigDecimal.ONE, 10, ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should get product by id")
    void shouldGetById() {
        Product p = testProduct();
        when(persistencePort.findById(p.getId())).thenReturn(Optional.of(p));

        Product result = service.getProductById(p.getId());
        assertEquals(p.getId(), result.getId());
    }

    @Test
    @DisplayName("Should throw when product not found")
    void shouldThrowNotFound() {
        UUID id = UUID.randomUUID();
        when(persistencePort.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.getProductById(id));
    }

    @Test
    @DisplayName("Should get all products")
    void shouldGetAll() {
        when(persistencePort.findAll()).thenReturn(List.of(testProduct()));

        List<Product> result = service.getAllProducts();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get products by status")
    void shouldGetByStatus() {
        when(persistencePort.findByStatus("ACTIVE")).thenReturn(List.of(testProduct()));

        List<Product> result = service.getProductsByStatus("ACTIVE");
        assertEquals(1, result.size());
    }
}
