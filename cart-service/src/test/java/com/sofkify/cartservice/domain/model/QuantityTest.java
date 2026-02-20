package com.sofkify.cartservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Quantity Value Object Tests")
class QuantityTest {

    @Test
    @DisplayName("Should create quantity with valid value")
    void shouldCreateQuantityWithValidValue() {
        // Act - This should fail because Quantity class doesn't exist yet
        Quantity quantity = new Quantity(5);

        // Assert
        assertEquals(5, quantity.getValue());
    }

    @Test
    @DisplayName("Should throw exception when quantity is zero")
    void shouldThrowExceptionWhenQuantityIsZero() {
        // Act & Assert - Quantity class doesn't exist yet
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Quantity(0));

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when quantity is negative")
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Quantity(-1));

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should add quantities")
    void shouldAddQuantities() {
        // Arrange
        Quantity quantity1 = new Quantity(3);
        Quantity quantity2 = new Quantity(2);

        // Act - Method doesn't exist yet
        Quantity result = quantity1.add(quantity2);

        // Assert
        assertEquals(5, result.getValue());
    }

    @Test
    @DisplayName("Should subtract quantities")
    void shouldSubtractQuantities() {
        // Arrange
        Quantity quantity1 = new Quantity(5);
        Quantity quantity2 = new Quantity(2);

        // Act - Method doesn't exist yet
        Quantity result = quantity1.subtract(quantity2);

        // Assert
        assertEquals(3, result.getValue());
    }

    @Test
    @DisplayName("Should throw exception when subtraction results in zero or negative")
    void shouldThrowExceptionWhenSubtractionResultsInZeroOrNegative() {
        // Arrange
        Quantity quantity1 = new Quantity(2);
        Quantity quantity2 = new Quantity(3);

        // Act & Assert - Method doesn't exist yet
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> quantity1.subtract(quantity2));

        assertEquals("Subtraction result must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should compare quantities correctly")
    void shouldCompareQuantitiesCorrectly() {
        // Arrange
        Quantity small = new Quantity(2);
        Quantity large = new Quantity(5);
        Quantity equal = new Quantity(2);

        // Assert - Methods don't exist yet
        assertTrue(small.isLessThan(large));
        assertTrue(large.isGreaterThan(small));
        assertTrue(small.isEqualTo(equal));
        assertFalse(small.isGreaterThan(large));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10, 100, 1000})
    @DisplayName("Should accept valid positive quantities")
    void shouldAcceptValidPositiveQuantities(int value) {
        // Act & Assert
        assertDoesNotThrow(() -> new Quantity(value));
        Quantity quantity = new Quantity(value);
        assertEquals(value, quantity.getValue());
    }

    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        // Arrange
        Quantity quantity = new Quantity(5);
        
        // Act - Test that operations return new instances
        Quantity added = quantity.add(new Quantity(3));
        
        // Assert - Original should be unchanged
        assertEquals(5, quantity.getValue());
        assertEquals(8, added.getValue());
    }

    @Test
    @DisplayName("Should implement equals and hashCode properly")
    void shouldImplementEqualsAndHashCodeProperly() {
        // Arrange
        Quantity quantity1 = new Quantity(5);
        Quantity quantity2 = new Quantity(5);
        Quantity quantity3 = new Quantity(10);

        // Assert
        assertEquals(quantity1, quantity2);
        assertNotEquals(quantity1, quantity3);
        assertEquals(quantity1.hashCode(), quantity2.hashCode());
    }

    @Test
    @DisplayName("Should create quantity from string")
    void shouldCreateQuantityFromString() {
        // Act - Static factory method doesn't exist yet
        Quantity quantity = Quantity.of("5");

        // Assert
        assertEquals(5, quantity.getValue());
    }

    @Test
    @DisplayName("Should throw exception when creating quantity from invalid string")
    void shouldThrowExceptionWhenCreatingQuantityFromInvalidString() {
        // Act & Assert - Static factory method doesn't exist yet
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
            () -> Quantity.of("abc"));
        
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
            () -> Quantity.of("-5"));
        
        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class,
            () -> Quantity.of("0"));

        assertTrue(exception1.getMessage().contains("Invalid quantity format"));
        assertEquals("Quantity must be greater than zero", exception2.getMessage());
        assertEquals("Quantity must be greater than zero", exception3.getMessage());
    }
}