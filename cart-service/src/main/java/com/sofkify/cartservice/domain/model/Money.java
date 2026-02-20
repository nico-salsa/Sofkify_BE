package com.sofkify.cartservice.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Money value object representing a monetary amount with currency.
 * Immutable and thread-safe.
 */
public record Money(BigDecimal amount, String currency) {
    
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add money with different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
    }
}