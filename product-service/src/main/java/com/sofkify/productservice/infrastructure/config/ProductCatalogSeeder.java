package com.sofkify.productservice.infrastructure.config;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCatalogSeeder implements ApplicationRunner {

    private final ProductPersistencePort productPersistencePort;
    private final CreateProductUseCase createProductUseCase;

    @Value("${sofkify.seed.products.enabled:true}")
    private boolean seedEnabled;

    @Override
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            log.info("Product catalog seed disabled by configuration");
            return;
        }

        if (!productPersistencePort.findAll().isEmpty()) {
            log.info("Product catalog seed skipped: products table already has data");
            return;
        }

        List<CreateProductCommand> defaultCatalog = List.of(
            new CreateProductCommand(
                "Laptop Pro 16",
                "Laptop de alto rendimiento para trabajo y gaming",
                "LP-16-001",
                new BigDecimal("1299.99"),
                25
            ),
            new CreateProductCommand(
                "Mouse Inalámbrico Ergo",
                "Mouse ergonómico con conexión bluetooth",
                "MS-ERGO-002",
                new BigDecimal("39.90"),
                120
            ),
            new CreateProductCommand(
                "Teclado Mecánico RGB",
                "Teclado mecánico switch blue con iluminación RGB",
                "KB-RGB-003",
                new BigDecimal("89.50"),
                80
            ),
            new CreateProductCommand(
                "Monitor 27 QHD",
                "Monitor 27 pulgadas resolución QHD 165Hz",
                "MN-QHD-004",
                new BigDecimal("319.00"),
                40
            ),
            new CreateProductCommand(
                "Auriculares Studio",
                "Auriculares over-ear con cancelación de ruido",
                "HP-STUDIO-005",
                new BigDecimal("149.99"),
                60
            )
        );

        defaultCatalog.forEach(createProductUseCase::createProduct);
        log.info("Product catalog seed completed with {} products", defaultCatalog.size());
    }
}
