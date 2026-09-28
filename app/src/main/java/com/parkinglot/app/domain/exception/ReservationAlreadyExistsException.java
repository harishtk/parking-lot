package com.parkinglot.app.domain.exception;

import com.parkinglot.app.domain.valueobject.RegistrationNumber;

public class ReservationAlreadyExistsException extends RuntimeException {
    public ReservationAlreadyExistsException(RegistrationNumber registrationNumber) {
        super("Reservation with registration " + registrationNumber.value() + " already exists");
    }
}

