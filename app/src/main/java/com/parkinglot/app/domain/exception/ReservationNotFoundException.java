package com.parkinglot.app.domain.exception;

import com.parkinglot.app.domain.valueobject.ReservationId;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(ReservationId reservationId) {
        super("Reservation with value " + reservationId + " not found");
    }
}
