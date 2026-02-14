package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateProductService implements CreateProductUseCase {

    private final ProductPersistencePort productPersistencePort;

    @Override
    public Product createProduct(Product product) {
        Product productCreated = Product.create(
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock()
        );
        return productPersistencePort.save(productCreated);
    }
}
