package com.sofkify.cartservice.domain.model;

public enum CartStatus {
    ACTIVE("Active"),
    CONFIRMED("Confirmed"),
    EXPIRED("Expired");
    
    private final String displayName;
    
    CartStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isModifiable() {
        return this == ACTIVE;
    }
    
    public static boolean canTransitionTo(CartStatus from, CartStatus to) {
        if (from == to) return false;
        
        return switch (from) {
            case ACTIVE -> to == CONFIRMED || to == EXPIRED;
            case CONFIRMED, EXPIRED -> false;
        };
    }
}
