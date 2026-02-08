package com.sofkify.productservice.domain.ports.out;

import com.sofkify.productservice.domain.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    
    Optional<Product> findById(UUID id);
    
    Product save(Product product);
    
}
