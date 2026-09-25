package com.parkinglot.app.application.service;

import com.google.inject.Inject;
import com.parkinglot.app.domain.model.ParkingLot;
import com.parkinglot.app.domain.repository.ParkingLotRepository;

public class DefaultParkingService implements ParkingService {

    private final ParkingLotRepository parkingLotRepository;

    @Inject
    public DefaultParkingService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    @Override
    public void createParkingLot(int floors, int carSpotsPerFloor, int bikeSpotsPerFloor) {

        ParkingLot parkingLot =
                ParkingLot.create(
                        floors,
                        carSpotsPerFloor,
                        bikeSpotsPerFloor
                );

        parkingLotRepository.save(parkingLot);
    }
}
