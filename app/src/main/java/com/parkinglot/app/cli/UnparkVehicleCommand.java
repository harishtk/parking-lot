package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.cli.converter.RegistrationNumberConverter;
import com.parkinglot.app.cli.io.ParkingOutput;
import com.parkinglot.app.domain.model.Ticket;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.repository.ParkingLotRepository;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Optional;
import java.util.concurrent.Callable;

@Command(
        name = "unpark",
        description = "Unparks a vehicle",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class UnparkVehicleCommand implements Callable<Integer> {

    @Option(names = {"-v", "--vehicle", "-r", "--registration"},
            required = true,
            paramLabel = "REGISTRATION",
            converter = RegistrationNumberConverter.class,
            description = "Vehicle registration, for example KA05AB1234"
    )
    private RegistrationNumber registrationNumber;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;


    private final ParkingService parkingService;

    @Inject
    public UnparkVehicleCommand(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {
        Optional<Ticket> ticket = parkingService.unparkVehicle(registrationNumber);
        if (ticket.isPresent()) {
            spec.commandLine().getOut().println(CommandLine.Help.Ansi.AUTO.string("@|bold,green Vehicle released successfully|@"));
            ParkingOutput.printTicket(spec, ticket.get());
            return 0;
        } else {
            String message = "ERROR FAILED: Unable to release vehicle: " + registrationNumber.value();
            ParkingOutput.printErr(spec, message);
            return 1;
        }
    }
}
