package com.parkinglot.app.domain.exception;

public class ParkingSpotUnavailableException extends RuntimeException {
    public ParkingSpotUnavailableException() {
        super("No parking spots are available right now.");
    }
}
