package com.sofkify.orderservice.domain.valueobject;

import java.util.Objects;

/**
 * Value Object que representa una cantidad de productos.
 * Inmutable y garantiza que siempre sea un número entero positivo.
 */
public record Quantity(int value) {

    public Quantity {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero, got: " + value);
        }
    }

    public Quantity add(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        return new Quantity(this.value + other.value);
    }

    public Quantity subtract(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        int result = this.value - other.value;
        if (result <= 0) {
            throw new IllegalArgumentException("Resulting quantity must be positive, got: " + result);
        }
        return new Quantity(result);
    }

    public Quantity multiply(int factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Multiplication factor must be positive, got: " + factor);
        }
        return new Quantity(this.value * factor);
    }

    public boolean isGreaterThan(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        return this.value > other.value;
    }

    public boolean isLessThan(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        return this.value < other.value;
    }

    public boolean equals(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        return this.value == other.value;
    }

    public static Quantity one() {
        return new Quantity(1);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}