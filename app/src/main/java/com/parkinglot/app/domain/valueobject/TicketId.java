package com.parkinglot.app.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public record TicketId(@JsonValue String value) implements Serializable {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public TicketId {
        if (null == value || value.isEmpty()) {
            throw new IllegalArgumentException("TicketId's value cannot be null or empty.");
        }
    }
}
