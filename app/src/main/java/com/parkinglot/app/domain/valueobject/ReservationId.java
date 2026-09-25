package com.parkinglot.app.domain.valueobject;

import java.io.Serializable;

public record ReservationId(String id) implements Serializable {

    public ReservationId {
        if (null == id || id.isEmpty()) {
            throw new IllegalArgumentException("ReservationId's value cannot be null or empty.");
        }
    }
}

