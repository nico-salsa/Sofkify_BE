package com.sofkify.orderservice.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Quantity Value Object Tests")
class QuantityTest {

    @Test
    @DisplayName("Should create valid Quantity")
    void shouldCreateValidQuantity() {
        Quantity q = new Quantity(5);
        assertEquals(5, q.value());
    }

    @Test
    @DisplayName("Should reject zero")
    void shouldRejectZero() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(0));
    }

    @Test
    @DisplayName("Should reject negative")
    void shouldRejectNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(-1));
    }

    @Test
    @DisplayName("Should add quantities")
    void shouldAdd() {
        Quantity a = new Quantity(3);
        Quantity b = new Quantity(2);
        assertEquals(5, a.add(b).value());
    }

    @Test
    @DisplayName("Should subtract quantities")
    void shouldSubtract() {
        Quantity a = new Quantity(5);
        Quantity b = new Quantity(2);
        assertEquals(3, a.subtract(b).value());
    }

    @Test
    @DisplayName("Should reject non-positive subtraction result")
    void shouldRejectNonPositiveSubtraction() {
        Quantity a = new Quantity(2);
        Quantity b = new Quantity(2);
        assertThrows(IllegalArgumentException.class, () -> a.subtract(b));
    }

    @Test
    @DisplayName("Should multiply by factor")
    void shouldMultiply() {
        Quantity q = new Quantity(3);
        assertEquals(6, q.multiply(2).value());
    }

    @Test
    @DisplayName("Should reject non-positive factor")
    void shouldRejectNonPositiveFactor() {
        Quantity q = new Quantity(3);
        assertThrows(IllegalArgumentException.class, () -> q.multiply(0));
        assertThrows(IllegalArgumentException.class, () -> q.multiply(-1));
    }

    @Test
    @DisplayName("Should compare greater than")
    void shouldCompareGreaterThan() {
        assertTrue(new Quantity(5).isGreaterThan(new Quantity(3)));
        assertFalse(new Quantity(3).isGreaterThan(new Quantity(5)));
    }

    @Test
    @DisplayName("Should compare less than")
    void shouldCompareLessThan() {
        assertTrue(new Quantity(3).isLessThan(new Quantity(5)));
        assertFalse(new Quantity(5).isLessThan(new Quantity(3)));
    }

    @Test
    @DisplayName("Should compare equal to")
    void shouldCompareEqualTo() {
        assertTrue(new Quantity(3).isEqualTo(new Quantity(3)));
        assertFalse(new Quantity(3).isEqualTo(new Quantity(5)));
    }

    @Test
    @DisplayName("Should compare greater than or equal")
    void shouldCompareGreaterOrEqual() {
        assertTrue(new Quantity(5).isGreaterThanOrEqualTo(new Quantity(3)));
        assertTrue(new Quantity(3).isGreaterThanOrEqualTo(new Quantity(3)));
    }

    @Test
    @DisplayName("Should compare less than or equal")
    void shouldCompareLessOrEqual() {
        assertTrue(new Quantity(3).isLessThanOrEqualTo(new Quantity(5)));
        assertTrue(new Quantity(3).isLessThanOrEqualTo(new Quantity(3)));
    }

    @Test
    @DisplayName("Should return one")
    void shouldReturnOne() {
        assertEquals(1, Quantity.one().value());
    }

    @Test
    @DisplayName("toString should return value string")
    void toStringShouldReturnValueString() {
        assertEquals("5", new Quantity(5).toString());
    }

    @Test
    @DisplayName("Should reject null in add")
    void shouldRejectNullInAdd() {
        assertThrows(NullPointerException.class, () -> new Quantity(1).add(null));
    }

    @Test
    @DisplayName("Should reject null in subtract")
    void shouldRejectNullInSubtract() {
        assertThrows(NullPointerException.class, () -> new Quantity(5).subtract(null));
    }
}
