package com.parkinglot.app.application.service;

public interface ParkingService {

    void createParkingLot(
            int floors,
            int carSpotsPerFloor,
            int bikeSpotsPerFloor
    );
}
