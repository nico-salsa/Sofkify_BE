package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.exception.DuplicateSkuException;
import com.sofkify.productservice.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CreateProductService Tests")
class CreateProductServiceTest {

    @Mock private ProductPersistencePort persistencePort;
    private CreateProductService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CreateProductService(persistencePort);
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProduct() {
        CreateProductCommand cmd = new CreateProductCommand("Laptop", "Desc", "SKU-001", BigDecimal.valueOf(999.99), 10);
        when(persistencePort.existsBySku("SKU-001")).thenReturn(false);
        when(persistencePort.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = service.createProduct(cmd);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(persistencePort).save(any(Product.class));
    }

    @Test
    @DisplayName("Should reject duplicate SKU")
    void shouldRejectDuplicateSku() {
        CreateProductCommand cmd = new CreateProductCommand("P", "D", "SKU-DUP", BigDecimal.ONE, 1);
        when(persistencePort.existsBySku("SKU-DUP")).thenReturn(true);

        assertThrows(DuplicateSkuException.class, () -> service.createProduct(cmd));
        verify(persistencePort, never()).save(any());
    }
}
