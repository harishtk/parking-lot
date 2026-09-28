package com.parkinglot.app.application.exception;

import com.parkinglot.app.domain.valueobject.RegistrationNumber;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(RegistrationNumber registrationNumber) {
        super("Vehicle with registration number " + registrationNumber.value() + " not found");
    }
}
