package com.sofkify.productservice.infrastructure.web.controller;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.in.GetProductUseCase;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.web.dto.request.CreateProductRequest;
import com.sofkify.productservice.infrastructure.web.dto.response.ProductResponse;
import com.sofkify.productservice.infrastructure.web.mapper.ProductDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ProductDtoMapper dtoMapper;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = dtoMapper.toDomain(request);
        Product createdProduct = createProductUseCase.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toResponse(createdProduct));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam(required = false) String status) {

        List<Product> products;
        if (status != null && !status.trim().isEmpty()) {
            products = getProductUseCase.getProductsByStatus(status.toUpperCase());
        } else {
            products = getProductUseCase.getAllProducts();
        }

        List<ProductResponse> response = products.stream()
            .map(dtoMapper::toResponse)
            .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return getProductUseCase.getProductById(id)
            .map(product -> ResponseEntity.ok(dtoMapper.toResponse(product)))
            .orElse(ResponseEntity.notFound().build());
    }
}
