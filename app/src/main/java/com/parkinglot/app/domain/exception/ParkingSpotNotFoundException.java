package com.parkinglot.app.domain.exception;

import com.parkinglot.app.domain.valueobject.SpotId;

public class ParkingSpotNotFoundException extends RuntimeException {
    public ParkingSpotNotFoundException(SpotId spotId) {
        super("Parking spot with id " + spotId + " not found");
    }
}
