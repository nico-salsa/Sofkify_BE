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

            // Drop the unique constraint on customer_id so a customer can have
            // multiple carts (e.g., a new ACTIVE cart after confirming a previous one).
            jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.table_constraints
                        WHERE table_name = 'carts'
                          AND constraint_type = 'UNIQUE'
                          AND constraint_name LIKE '%customer_id%'
                    ) THEN
                        EXECUTE 'ALTER TABLE carts DROP CONSTRAINT ' ||
                            (SELECT constraint_name
                             FROM information_schema.table_constraints
                             WHERE table_name = 'carts'
                               AND constraint_type = 'UNIQUE'
                               AND constraint_name LIKE '%customer_id%'
                             LIMIT 1);
                    END IF;
                END
                $$;
                """);
            // Also drop Hibernate-generated unique constraint by pattern
            jdbcTemplate.execute("""
                DO $$
                DECLARE
                    r RECORD;
                BEGIN
                    FOR r IN
                        SELECT con.conname
                        FROM pg_constraint con
                        JOIN pg_class rel ON rel.oid = con.conrelid
                        JOIN pg_attribute att ON att.attrelid = rel.oid AND att.attnum = ANY(con.conkey)
                        WHERE rel.relname = 'carts'
                          AND con.contype = 'u'
                          AND att.attname = 'customer_id'
                    LOOP
                        EXECUTE 'ALTER TABLE carts DROP CONSTRAINT IF EXISTS ' || r.conname;
                    END LOOP;
                END
                $$;
                """);
            logger.info("Removed unique constraint on customer_id (allows multiple carts per customer)");
        };
    }
}

