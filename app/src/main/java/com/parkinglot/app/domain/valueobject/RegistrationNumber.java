package com.parkinglot.app.domain.valueobject;

import java.io.Serializable;

public record RegistrationNumber(String registrationNumber) implements Serializable {

    private static final String VEHICLE_REGEX = "^([A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4})|([0-9]{2}BH[0-9]{4}[A-Z]{1,2})$";

    public RegistrationNumber {
        if (null == registrationNumber || registrationNumber.isEmpty()) {
            throw new IllegalArgumentException("registrationNumber cannot be null or empty.");
        }

        // Sanitize
        registrationNumber = registrationNumber.replaceAll("\\s+", "").toUpperCase();

        if (!registrationNumber.matches(VEHICLE_REGEX)) {
            throw new IllegalArgumentException("registrationNumber is invalid.");
        }
    }
}

