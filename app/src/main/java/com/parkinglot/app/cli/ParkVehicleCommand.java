package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.cli.converter.RegistrationNumberConverter;
import com.parkinglot.app.cli.io.ParkingOutput;
import com.parkinglot.app.domain.model.Ticket;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.Optional;
import java.util.concurrent.Callable;

@Command(
        name = "park",
        description = "Park a vehicle and issue a ticket.",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class ParkVehicleCommand implements Callable<Integer> {

    @Option(names = {"-v", "--vehicle", "-r", "--registration"},
            required = true,
            paramLabel = "REGISTRATION",
            converter = RegistrationNumberConverter.class,
            description = "Vehicle registration, for example KA05AB1234"
    )
    private RegistrationNumber registrationNumber;

    @Option(names = {"-t", "--type"},
            required = true,
            paramLabel = "TYPE",
            description = "Vehicle type: ${COMPLETION-CANDIDATES}."
    )
    private VehicleType vehicleType;

    @Spec
    private CommandSpec spec;

    private final ParkingService parkingService;

    @Inject
    public ParkVehicleCommand(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {
        Ticket ticket = parkingService.parkVehicle(
                registrationNumber,
                vehicleType
        );

        ParkingOutput.success(spec, "Vehicle parked.");
        ParkingOutput.detail(spec, "Registration", registrationNumber.value());
        ParkingOutput.printTicket(spec, ticket);

        return 0;
    }
}
