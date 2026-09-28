package com.parkinglot.app.infrastructure.persistence.local;

import com.google.inject.Inject;
import com.parkinglot.app.domain.model.ParkingLot;
import com.parkinglot.app.domain.repository.ParkingLotRepository;
import com.parkinglot.app.domain.repository.ParkingLotRepositoryException;

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
            return Optional.ofNullable(storageManager.loadData(DATA_FILENAME, ParkingLot.class));
        } catch (IOException e) {
            throw new ParkingLotRepositoryException(
                    "Unable to load the parking lot.", e);
        }
    }

    @Override
    public void save(ParkingLot parkingLot) {
        try {
            storageManager.saveData(DATA_FILENAME, parkingLot);
        } catch (IOException e) {
            throw new ParkingLotRepositoryException(
                    "Unable to save the parking lot.", e);
        }
    }

    @Override
    public boolean delete() {
        try {
            return storageManager.deleteData(DATA_FILENAME);
        } catch (IOException e) {
            throw new ParkingLotRepositoryException(
                    "Unable to delete the parking lot.", e);
        }
    }
}
