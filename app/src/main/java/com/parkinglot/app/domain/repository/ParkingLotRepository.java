package com.parkinglot.app.domain.repository;

import com.parkinglot.app.domain.model.ParkingLot;

import java.util.Optional;

public interface ParkingLotRepository {

    Optional<ParkingLot> load();

    boolean save(ParkingLot parkingLot);
}
