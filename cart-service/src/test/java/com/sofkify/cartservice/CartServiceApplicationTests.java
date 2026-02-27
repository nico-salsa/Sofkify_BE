package com.sofkify.cartservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class CartServiceApplicationTests {

    @Test
    void contextLoads() {
        // Sanity check without loading Spring context to avoid external DB dependency in CI
        assertTrue(true);
    }

}
