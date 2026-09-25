package com.parkinglot.app.infrastructure.persistence.local;

import com.google.inject.Inject;
import com.parkinglot.app.domain.model.ParkingLot;
import com.parkinglot.app.domain.repository.ParkingLotRepository;

import java.io.IOException;
import java.util.Optional;

public class LocalParkingLotRepository implements ParkingLotRepository {

    private static final String DATA_FILENAME = "parking_lot.json";

    private final CliStorageManager storageManager;

    @Inject
    public LocalParkingLotRepository(CliStorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public Optional<ParkingLot> load() {
        try {
            return Optional.of(storageManager.loadData(DATA_FILENAME, ParkingLot.class));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean save(ParkingLot parkingLot) {
        try {
            storageManager.saveData(DATA_FILENAME, parkingLot);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
