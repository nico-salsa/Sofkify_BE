package com.sofkify.cartservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create money with valid amount and currency")
    void shouldCreateMoneyWithValidAmountAndCurrency() {
        // Act - This should fail because Money class doesn't exist yet
        Money money = new Money(BigDecimal.valueOf(99.99), "USD");

        // Assert
        assertEquals(BigDecimal.valueOf(99.99), money.getAmount());
        assertEquals("USD", money.getCurrency());
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        // Act & Assert - Money class doesn't exist yet
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Money(null, "USD"));

        assertEquals("Amount cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when amount is negative")
    void shouldThrowExceptionWhenAmountIsNegative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Money(BigDecimal.valueOf(-10.00), "USD"));

        assertEquals("Amount must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when currency is null or empty")
    void shouldThrowExceptionWhenCurrencyIsNullOrEmpty() {
        // Act & Assert
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
            () -> new Money(BigDecimal.valueOf(100.00), null));
        assertEquals("Currency cannot be null or empty", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
            () -> new Money(BigDecimal.valueOf(100.00), ""));
        assertEquals("Currency cannot be null or empty", exception2.getMessage());
    }

    @Test
    @DisplayName("Should add two money objects with same currency")
    void shouldAddTwoMoneyObjectsWithSameCurrency() {
        // Arrange
        Money money1 = new Money(BigDecimal.valueOf(50.00), "USD");
        Money money2 = new Money(BigDecimal.valueOf(25.00), "USD");

        // Act - Method doesn't exist yet
        Money result = money1.add(money2);

        // Assert
        assertEquals(BigDecimal.valueOf(75.00), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    @DisplayName("Should throw exception when adding money with different currencies")
    void shouldThrowExceptionWhenAddingMoneyWithDifferentCurrencies() {
        // Arrange
        Money money1 = new Money(BigDecimal.valueOf(50.00), "USD");
        Money money2 = new Money(BigDecimal.valueOf(25.00), "EUR");

        // Act & Assert - Method doesn't exist yet
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> money1.add(money2));

        assertEquals("Cannot add money with different currencies", exception.getMessage());
    }

    @Test
    @DisplayName("Should multiply money by quantity")
    void shouldMultiplyMoneyByQuantity() {
        // Arrange
        Money money = new Money(BigDecimal.valueOf(15.50), "USD");

        // Act - Method doesn't exist yet
        Money result = money.multiply(3);

        // Assert
        assertEquals(BigDecimal.valueOf(46.50), result.getAmount());
        assertEquals("USD", result.getCurrency());
    }

    @ParameterizedTest
    @ValueSource(strings = {"USD", "EUR", "GBP", "JPY"})
    @DisplayName("Should support common currencies")
    void shouldSupportCommonCurrencies(String currency) {
        // Act & Assert
        assertDoesNotThrow(() -> new Money(BigDecimal.valueOf(100.00), currency));
    }

    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        // Arrange
        Money money = new Money(BigDecimal.valueOf(100.00), "USD");
        
        // Act - Test that operations return new instances
        Money doubled = money.multiply(2);
        
        // Assert - Original should be unchanged
        assertEquals(BigDecimal.valueOf(100.00), money.getAmount());
        assertEquals(BigDecimal.valueOf(200.00), doubled.getAmount());
    }

    @Test
    @DisplayName("Should implement equals and hashCode properly")
    void shouldImplementEqualsAndHashCodeProperly() {
        // Arrange
        Money money1 = new Money(BigDecimal.valueOf(100.00), "USD");
        Money money2 = new Money(BigDecimal.valueOf(100.00), "USD");
        Money money3 = new Money(BigDecimal.valueOf(100.00), "EUR");

        // Assert
        assertEquals(money1, money2);
        assertNotEquals(money1, money3);
        assertEquals(money1.hashCode(), money2.hashCode());
    }
}