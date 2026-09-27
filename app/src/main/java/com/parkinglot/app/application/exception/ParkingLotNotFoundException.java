package com.parkinglot.app.application.exception;

public class ParkingLotNotFoundException extends RuntimeException {
    public ParkingLotNotFoundException() {
        super("Parking Lot Not Found");
    }
}
