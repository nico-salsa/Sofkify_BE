package com.sofkify.cartservice.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CartSchemaConfig {

    private static final Logger logger = LoggerFactory.getLogger(CartSchemaConfig.class);

    @Bean
    ApplicationRunner ensureCartStatusConstraint(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.tables
                        WHERE table_name = 'carts'
                    ) THEN
                        ALTER TABLE carts DROP CONSTRAINT IF EXISTS carts_status_check;
                        ALTER TABLE carts
                            ADD CONSTRAINT carts_status_check
                            CHECK (status IN ('ACTIVE', 'CONFIRMED', 'EXPIRED'));
                    END IF;
                END
                $$;
                """);
            logger.info("Verified carts status constraint for ACTIVE/CONFIRMED/EXPIRED");
        };
    }
}

