package com.sofkify.orderservice.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create Money from BigDecimal")
    void shouldCreateFromBigDecimal() {
        Money money = new Money(BigDecimal.valueOf(10.50));
        assertEquals(new BigDecimal("10.50"), money.amount());
    }

    @Test
    @DisplayName("Should create Money from double")
    void shouldCreateFromDouble() {
        Money money = new Money(10.50);
        assertEquals(new BigDecimal("10.50"), money.amount());
    }

    @Test
    @DisplayName("Should create Money from String")
    void shouldCreateFromString() {
        Money money = new Money("10.50");
        assertEquals(new BigDecimal("10.50"), money.amount());
    }

    @Test
    @DisplayName("Should reject null amount")
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new Money((BigDecimal) null));
    }

    @Test
    @DisplayName("Should reject negative amount")
    void shouldRejectNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.valueOf(-1)));
    }

    @Test
    @DisplayName("Should set scale to 2")
    void shouldSetScaleTo2() {
        Money money = new Money(BigDecimal.valueOf(10));
        assertEquals(new BigDecimal("10.00"), money.amount());
    }

    @Test
    @DisplayName("Should add two Money values")
    void shouldAdd() {
        Money a = new Money(BigDecimal.valueOf(10.50));
        Money b = new Money(BigDecimal.valueOf(5.25));
        Money result = a.add(b);
        assertEquals(new BigDecimal("15.75"), result.amount());
    }

    @Test
    @DisplayName("Should subtract two Money values")
    void shouldSubtract() {
        Money a = new Money(BigDecimal.valueOf(10.50));
        Money b = new Money(BigDecimal.valueOf(5.25));
        Money result = a.subtract(b);
        assertEquals(new BigDecimal("5.25"), result.amount());
    }

    @Test
    @DisplayName("Should multiply by int")
    void shouldMultiplyByInt() {
        Money money = new Money(BigDecimal.valueOf(10.00));
        Money result = money.multiply(3);
        assertEquals(new BigDecimal("30.00"), result.amount());
    }

    @Test
    @DisplayName("Should multiply by BigDecimal")
    void shouldMultiplyByBigDecimal() {
        Money money = new Money(BigDecimal.valueOf(10.00));
        Money result = money.multiply(new BigDecimal("2.5"));
        assertEquals(new BigDecimal("25.00"), result.amount());
    }

    @Test
    @DisplayName("Should compare greater than")
    void shouldCompareGreaterThan() {
        Money a = new Money(BigDecimal.valueOf(10));
        Money b = new Money(BigDecimal.valueOf(5));
        assertTrue(a.isGreaterThan(b));
        assertFalse(b.isGreaterThan(a));
    }

    @Test
    @DisplayName("Should compare less than")
    void shouldCompareLessThan() {
        Money a = new Money(BigDecimal.valueOf(5));
        Money b = new Money(BigDecimal.valueOf(10));
        assertTrue(a.isLessThan(b));
        assertFalse(b.isLessThan(a));
    }

    @Test
    @DisplayName("Should compare equal to")
    void shouldCompareEqualTo() {
        Money a = new Money(BigDecimal.valueOf(10));
        Money b = new Money(BigDecimal.valueOf(10));
        assertEquals(a, b); // Money is a record, equals() works
    }

    @Test
    @DisplayName("Should compare greater than or equal")
    void shouldCompareGreaterOrEqual() {
        Money a = new Money(BigDecimal.valueOf(10));
        Money b = new Money(BigDecimal.valueOf(10));
        assertTrue(a.isGreaterThanOrEqualTo(b));
    }

    @Test
    @DisplayName("Should compare less than or equal")
    void shouldCompareLessOrEqual() {
        Money a = new Money(BigDecimal.valueOf(10));
        Money b = new Money(BigDecimal.valueOf(10));
        assertTrue(a.isLessThanOrEqualTo(b));
    }

    @Test
    @DisplayName("Should check isZero")
    void shouldCheckIsZero() {
        assertTrue(Money.zero().isZero());
        assertFalse(new Money(BigDecimal.ONE).isZero());
    }

    @Test
    @DisplayName("Should return zero instance")
    void shouldReturnZero() {
        Money zero = Money.zero();
        assertEquals(new BigDecimal("0.00"), zero.amount());
    }

    @Test
    @DisplayName("toString should return amount string")
    void toStringShouldReturnAmountString() {
        Money money = new Money(BigDecimal.valueOf(10.50));
        assertEquals("10.50", money.toString());
    }
}
