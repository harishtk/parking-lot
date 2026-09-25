package com.parkinglot.app.domain.valueobject;

import java.io.Serializable;

public record AllocationId(String id) implements Serializable {

    public AllocationId {
        if (null == id || id.isEmpty()) {
            throw new IllegalArgumentException("AllocationId's value cannot be null or empty.");
        }
    }
}

