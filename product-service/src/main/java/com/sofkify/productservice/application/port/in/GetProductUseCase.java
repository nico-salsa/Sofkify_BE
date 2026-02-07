package com.sofkify.productservice.application.port.in;

import com.sofkify.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface GetProductUseCase {
    Optional<Product> getProductById(String id);
    List<Product> getAllProducts();
    List<Product> getProductsByStatus(String status);
}
