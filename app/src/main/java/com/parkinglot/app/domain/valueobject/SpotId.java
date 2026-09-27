package com.parkinglot.app.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public record SpotId(@JsonValue String id) implements Serializable {
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public SpotId {
        if (null == id || id.isEmpty()) {
            throw new IllegalArgumentException("SpotId's value cannot be null or empty.");
        }
    }
}
