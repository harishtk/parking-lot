package com.parkinglot.app.cli;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import com.google.inject.Inject;
import com.parkinglot.app.application.exception.ParkingLotNotFoundException;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.cli.io.ParkingOutput;
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
        ParkingLot parkingLot = parkingService.getParkingLot();

        ParkingLotMetaData metaData = parkingLot.describe();

        var out = spec.commandLine().getOut();
        ParkingOutput.heading(spec, "Parking lot");
        ParkingOutput.detail(spec, "Floors", metaData.numFloors());
        ParkingOutput.detail(spec, "Spots", metaData.numSpots());
        ParkingOutput.detail(spec, "Occupied spots", metaData.numOccupiedSpots());
        ParkingOutput.detail(spec, "Empty spots", metaData.numSpots() - metaData.numOccupiedSpots());
        ParkingOutput.detail(spec, "Tickets", metaData.numTickets());
        ParkingOutput.detail(spec, "Reservations", metaData.numReservations());

        if (showAllocations) {
            List<ParkingSpot> spots = parkingLot.findCandidateSpots().stream()
                    .sorted(java.util.Comparator.comparing(spot -> spot.id().id()))
                    .toList();
            out.println();
            ParkingOutput.heading(spec, "Spot availability");
            if (spots.isEmpty()) {
                out.println("  No spots to display.");
                return 0;
            }

            List<ColumnData<ParkingSpot>> columns = Arrays.asList(
                    new Column().header("Spot").with(spot -> spot.id().id()),
                    new Column().header("Type").with(spot -> spot.spotType().toString()),
                    new Column().header("Remaining capacity").with(spot -> Integer.toString(spot.remainingCapacity())),
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
