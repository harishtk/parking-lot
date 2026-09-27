package com.parkinglot.app.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public record ReservationId(@JsonValue String value) implements Serializable {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ReservationId {
        if (null == value || value.isEmpty()) {
            throw new IllegalArgumentException("ReservationId's value cannot be null or empty.");
        }
    }
}
