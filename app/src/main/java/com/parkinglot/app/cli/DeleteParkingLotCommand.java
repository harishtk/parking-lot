package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.domain.repository.ParkingLotRepository;
import com.parkinglot.app.infrastructure.persistence.local.CliStorageManager;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "delete",
        description = "Deletes the current parking lot."
)
public class DeleteParkingLotCommand implements Callable<Integer> {

    private final ParkingLotRepository repository;

    @Inject
    public DeleteParkingLotCommand(CliStorageManager storageManager, ParkingLotRepository repository) {
        this.repository = repository;
    }

    @Override
    public Integer call() throws Exception {
        if (repository.delete()) {
            System.out.println("Successfully deleted the parking lot.");
            return 0;
        } else {
            System.out.println("Failed to delete the parking lot.");
            return 1;
        }
    }
}
