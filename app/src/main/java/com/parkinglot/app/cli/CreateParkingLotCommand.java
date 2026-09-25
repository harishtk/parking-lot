package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(
        name = "create-lot",
        description = "Creates a parking lot"
)
public class CreateParkingLotCommand implements Callable<Integer> {

    @Option(
            names = {"--car-spots-per-floor"},
            description = "Car spots per floor",
            defaultValue = "1"
    )
    private int carSpotsPerFloor;

    @Option(
            names = {"--bike-spots-per-floor"},
            description = "Bike spots per floor",
            defaultValue = "1"
    )
    private int bikeSpotsPerFloor;

    @Option(
            names = {"--floors", "-f"},
            description = "The number of floors for parking lot.",
            defaultValue = "1")
    private int numFloors;

    private final ParkingService parkingService;

    @Inject
    public CreateParkingLotCommand(
            ParkingService parkingService
    ) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {

        parkingService.createParkingLot(
                numFloors,
                carSpotsPerFloor,
                bikeSpotsPerFloor
        );

        System.out.println("Parking Lot Created");

        return 0;
    }
}
