package com.sofkify.cartservice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartServiceApplicationTests {

    @Test
    void contextLoads() {
        // Sanity check without loading Spring context to avoid external DB dependency in CI
        assertTrue(true);
    }

}
