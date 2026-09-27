package com.parkinglot.app.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public record FloorId(@JsonValue String id) implements Serializable {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public FloorId {
        if (null == id || id.isEmpty()) {
            throw new IllegalArgumentException("FloorId's value cannot be null or empty.");
        }
    }
}
