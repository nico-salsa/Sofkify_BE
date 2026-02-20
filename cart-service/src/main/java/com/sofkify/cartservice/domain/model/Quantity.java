package com.sofkify.cartservice.domain.model;

/**
 * Quantity value object representing a positive integer quantity.
 * Immutable and thread-safe.
 */
public record Quantity(int value) {
    
    public Quantity {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
    
    public int getValue() {
        return value;
    }
    
    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }
    
    public Quantity subtract(Quantity other) {
        int result = this.value - other.value;
        if (result <= 0) {
            throw new IllegalArgumentException("Subtraction result must be greater than zero");
        }
        return new Quantity(result);
    }
    
    public boolean isLessThan(Quantity other) {
        return this.value < other.value;
    }
    
    public boolean isGreaterThan(Quantity other) {
        return this.value > other.value;
    }
    
    public boolean isEqualTo(Quantity other) {
        return this.value == other.value;
    }
    
    public static Quantity of(String valueStr) {
        try {
            int parsed = Integer.parseInt(valueStr.trim());
            return new Quantity(parsed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid quantity format: " + valueStr, e);
        }
    }
}