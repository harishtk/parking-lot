package com.parkinglot.app.domain.valueobject;

import java.io.Serializable;

public record SpotId(String id) implements Serializable {
    public SpotId {
        if (null == id || id.isEmpty()) {
            throw new IllegalArgumentException("SpotId's registrationNumber cannot be null or empty.");
        }
    }
}
