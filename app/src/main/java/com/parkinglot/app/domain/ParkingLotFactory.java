package com.parkinglot.app.domain;

import com.parkinglot.app.domain.model.ParkingLot;

public interface ParkingLotFactory {

    ParkingLot create(
            int floorCount,
            int carSpotsPerFloor,
            int bikeSpotsPerFloor
    );
}
