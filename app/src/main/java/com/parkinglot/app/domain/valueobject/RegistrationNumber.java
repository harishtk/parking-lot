package com.parkinglot.app.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public record RegistrationNumber(@JsonValue String value) implements Serializable {

    private static final String VEHICLE_REGEX = "^([A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4})|([0-9]{2}BH[0-9]{4}[A-Z]{1,2})$";

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public RegistrationNumber {
        if (null == value || value.isEmpty()) {
            throw new IllegalArgumentException("value cannot be null or empty.");
        }

        // Sanitize
        value = value.replaceAll("\\s+", "").toUpperCase();

        if (!value.matches(VEHICLE_REGEX)) {
            throw new IllegalArgumentException("value is invalid.");
        }
    }
}
