package com.sofkify.productservice.infrastructure.adapters.out.persistence;

import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.domain.ports.out.ProductRepositoryPort;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;
import com.sofkify.productservice.infrastructure.persistence.repository.JpaProductRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final JpaProductRepository jpaProductRepository;

    public ProductRepositoryAdapter(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaProductRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity productEntity = toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(productEntity);
        return toDomain(savedEntity);
    }

    private Product toDomain(ProductEntity entity) {
        return Product.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                entity.getStatus()
        );
    }

    private ProductEntity toEntity(Product product) {
        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStatus()
        );
    }
}
