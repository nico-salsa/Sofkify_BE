package com.sofkify.productservice.infrastructure.web.mapper;

import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.web.dto.request.CreateProductRequest;
import com.sofkify.productservice.infrastructure.web.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getStatus()
        );
    }

    public Product toDomain(CreateProductRequest request) {
        return Product.create(
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getStock()
        );
    }
}
