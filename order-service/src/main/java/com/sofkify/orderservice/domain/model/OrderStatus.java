package com.sofkify.orderservice.domain.model;

public enum OrderStatus {
    PENDING("Pending"),
    PENDING_PAYMENT("Pending Payment"),
    PAID("Paid"),
    CONFIRMED("Confirmed"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    FAILED("Failed");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED || this == FAILED;
    }
    
    public static boolean canTransitionTo(OrderStatus from, OrderStatus to) {
        if (from == to) return false;
        if (from.isTerminal()) return false;
        
        return switch (from) {
            case PENDING -> to == CONFIRMED || to == CANCELLED || to == FAILED;
            case PENDING_PAYMENT -> to == PAID || to == CANCELLED || to == FAILED;
            case PAID -> to == CONFIRMED || to == CANCELLED || to == FAILED;
            case CONFIRMED -> to == SHIPPED || to == CANCELLED || to == FAILED;
            case SHIPPED -> to == DELIVERED || to == FAILED;
            case DELIVERED, CANCELLED, FAILED -> false;
        };
    }
}
