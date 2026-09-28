package com.parkinglot.app.domain.repository;

import com.parkinglot.app.domain.model.ParkingLot;

import java.util.Optional;

public interface ParkingLotRepository {

    Optional<ParkingLot> load();

    void save(ParkingLot parkingLot);

    boolean delete();
}
