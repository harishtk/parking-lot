package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.cli.io.ParkingOutput;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(
        name = "create-lot",
        description = "Create a parking lot.",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class CreateParkingLotCommand implements Callable<Integer> {

    @Option(
            names = {"--car-spots-per-floor"},
            description = "Car spots per floor",
            defaultValue = "1",
            showDefaultValue = CommandLine.Help.Visibility.ON_DEMAND,
            paramLabel = "COUNT"
    )
    private int carSpotsPerFloor;

    @Option(
            names = {"--bike-spots-per-floor"},
            description = "Bike spots per floor",
            defaultValue = "1",
            showDefaultValue = CommandLine.Help.Visibility.ON_DEMAND,
            paramLabel = "COUNT"
    )
    private int bikeSpotsPerFloor;

    @Option(
            names = {"--floors", "-f"},
            description = "The number of floors for parking lot.",
            defaultValue = "1",
            showDefaultValue = CommandLine.Help.Visibility.ON_DEMAND,
            paramLabel = "COUNT"
    )
    private int numFloors;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

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

        ParkingOutput.success(spec, "Parking lot created.");
        ParkingOutput.detail(spec, "Floors", numFloors);
        ParkingOutput.detail(spec, "Car spots / floor", carSpotsPerFloor);
        ParkingOutput.detail(spec, "Bike spots / floor", bikeSpotsPerFloor);

        return 0;
    }
}
