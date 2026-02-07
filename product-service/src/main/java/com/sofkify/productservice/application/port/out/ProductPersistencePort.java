package com.sofkify.productservice.application.port.out;

import com.sofkify.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductPersistencePort {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    List<Product> findByStatus(String status);
}
