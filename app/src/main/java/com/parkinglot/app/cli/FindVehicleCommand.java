package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.cli.converter.RegistrationNumberConverter;
import com.parkinglot.app.cli.io.ParkingOutput;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(
        name = "find-vehicle",
        aliases = {"find"},
        description = "Find a parked vehicle.",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class FindVehicleCommand implements Callable<Integer> {

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
    public FindVehicleCommand(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {
        parkingService.findVehicle(registrationNumber)
                .ifPresentOrElse(ticket -> {
                            ParkingOutput.success(spec, "Vehicle found.");
                            ParkingOutput.detail(spec, "Registration", registrationNumber.value());
                            ParkingOutput.printTicket(spec, ticket);
                        },
                        () -> {
                            String message = "No parked vehicle found for " + registrationNumber.value() + ".";
                            ParkingOutput.printErr(spec, message);
                        }
                );

        return 0;
    }
}
