package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.cli.io.ParkingOutput;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "delete-lot",
        aliases = {"delete"},
        description = "Delete the current parking lot.",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class DeleteParkingLotCommand implements Callable<Integer> {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    private final ParkingService parkingService;

    @Inject
    public DeleteParkingLotCommand(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {
        if (parkingService.deleteParkingLot()) {
            ParkingOutput.success(spec, "Parking lot deleted.");
            return 0;
        } else {
            ParkingOutput.printErr(
                    spec,
                    "Failed to delete the parking lot."
            );
            return 1;
        }
    }
}
