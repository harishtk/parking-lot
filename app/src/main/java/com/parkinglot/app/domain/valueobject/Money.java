package com.parkinglot.app.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.math.BigDecimal;

public record Money(@JsonValue BigDecimal amount) implements Serializable {
    
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Money {
        if (null == amount) {
            throw new IllegalArgumentException("amount cannot be null.");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative.");
        }
    }
}
