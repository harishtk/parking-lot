package com.parkinglot.app.domain.valueobject;

import java.io.Serializable;

public record FloorId(String id) implements Serializable {

    public FloorId {
        if (null == id || id.isEmpty()) {
            throw new IllegalArgumentException("FloorId's registrationNumber cannot be null or empty.");
        }
    }
}

