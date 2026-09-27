package com.parkinglot.app.cli;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import com.google.inject.Inject;
import com.parkinglot.app.application.exception.ParkingLotNotFoundException;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.domain.model.ParkingLot;
import com.parkinglot.app.domain.model.ParkingLotMetaData;
import com.parkinglot.app.domain.model.ParkingSpot;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "status",
        description = "Display current status of the parking lot.",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class ParkingStatusCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-a", "--show-allocations"},
            description = "Show allocations",
            defaultValue = "false",
            showDefaultValue = CommandLine.Help.Visibility.ON_DEMAND
    )
    private boolean showAllocations;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    private final ParkingService parkingService;

    @Inject
    public ParkingStatusCommand(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {
        ParkingLot parkingLot = parkingService.getParkingLot()
                .orElseThrow(ParkingLotNotFoundException::new);

        ParkingLotMetaData metaData = parkingLot.describe();

        var out = spec.commandLine().getOut();
        out.printf("  %14s %d%n", "Total Floors:", metaData.numFloors());
        out.printf("  %14s %d%n", "Total Spots:", metaData.numSpots());
        out.printf("  %14s %d%n", "Total Tickets:", metaData.numTickets());
        out.printf("  %14s %d%n", "Total Reservations:", metaData.numReservations());
        out.printf("  %14s %d%n", "Occupied Spots:", metaData.numOccupiedSpots());

        if (showAllocations) {
            List<ParkingSpot> spots = parkingLot.findCandidateSpots();

            List<ColumnData<ParkingSpot>> columns = Arrays.asList(
                    new Column().header("Spot").with(spot -> spot.id().id()),
                    new Column().header("Type").with(spot -> spot.spotType().toString()),
                    new Column().header("Used / Capacity").with(spot -> Integer.toString(spot.remainingCapacity())),
                    new Column().header("Status").with(spot -> {
                        if (spot.isFull()) {
                            return "FULL";
                        } else if (spot.isEmpty()) {
                            return "FREE";
                        } else {
                            return "PARTIAL";
                        }
                    })
            );

            out.println(AsciiTable.getTable(spots, columns));
        }

        return 0;
    }
}