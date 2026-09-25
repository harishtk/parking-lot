package com.parkinglot.app.domain.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;

public record Money(BigDecimal amount) implements Serializable {
    
    public Money {
        if (null == amount) {
            throw new IllegalArgumentException("amount cannot be null.");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative.");
        }
    }
}
