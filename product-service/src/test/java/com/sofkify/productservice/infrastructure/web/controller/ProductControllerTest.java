package com.sofkify.productservice.infrastructure.web.controller;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.in.GetProductUseCase;
import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.web.dto.request.CreateProductRequest;
import com.sofkify.productservice.infrastructure.web.dto.response.ProductResponse;
import com.sofkify.productservice.infrastructure.web.mapper.ProductDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Mock private CreateProductUseCase createProductUseCase;
    @Mock private GetProductUseCase getProductUseCase;
    @Mock private ProductDtoMapper dtoMapper;

    private ProductController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ProductController(createProductUseCase, getProductUseCase, dtoMapper);
    }

    private Product testProduct() {
        return Product.reconstitute(UUID.randomUUID(), "P", "D", "SKU", BigDecimal.TEN, 10, ProductStatus.ACTIVE);
    }

    private ProductResponse testResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getSku(),
            p.getPrice(), p.getStock(), p.getStatus());
    }

    @Test
    @DisplayName("Should create product")
    void shouldCreateProduct() {
        Product product = testProduct();
        ProductResponse resp = testResponse(product);
        CreateProductRequest request = new CreateProductRequest("P", "D", "SKU", BigDecimal.TEN, 10);

        when(dtoMapper.toCommand(request)).thenReturn(new CreateProductCommand("P", "D", "SKU", BigDecimal.TEN, 10));
        when(createProductUseCase.createProduct(any())).thenReturn(product);
        when(dtoMapper.toDto(product)).thenReturn(resp);

        var response = controller.createProduct(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(resp, response.getBody());
    }

    @Test
    @DisplayName("Should get product by id")
    void shouldGetProductById() {
        Product product = testProduct();
        ProductResponse resp = testResponse(product);

        when(getProductUseCase.getProductById(product.getId())).thenReturn(product);
        when(dtoMapper.toDto(product)).thenReturn(resp);

        var response = controller.getProductById(product.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resp, response.getBody());
    }

    @Test
    @DisplayName("Should get all products without status filter")
    void shouldGetAllProducts() {
        Product product = testProduct();
        ProductResponse resp = testResponse(product);

        when(getProductUseCase.getAllProducts()).thenReturn(List.of(product));
        when(dtoMapper.toDto(product)).thenReturn(resp);

        var response = controller.getAllProducts(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should get products by status")
    void shouldGetProductsByStatus() {
        Product product = testProduct();
        ProductResponse resp = testResponse(product);

        when(getProductUseCase.getProductsByStatus("ACTIVE")).thenReturn(List.of(product));
        when(dtoMapper.toDto(product)).thenReturn(resp);

        var response = controller.getAllProducts("active");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
