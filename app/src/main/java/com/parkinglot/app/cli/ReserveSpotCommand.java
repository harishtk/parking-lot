package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ReservationService;
import com.parkinglot.app.cli.converter.RegistrationNumberConverter;
import com.parkinglot.app.cli.io.ParkingOutput;
import com.parkinglot.app.domain.model.Reservation;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(
        name = "reserve",
        description = "Reserve for a vehicle (availability not guaranteed).",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class ReserveSpotCommand implements Callable<Integer> {

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

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    private final ReservationService reservationService;

    @Inject
    public ReserveSpotCommand(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public Integer call() throws Exception {
        Reservation reservation = reservationService
                .reserve(
                        registrationNumber,
                        vehicleType
                );
        ParkingOutput.success(spec, "Reservation created.");
        ParkingOutput.detail(spec, "Reservation", reservation.id().value());
        ParkingOutput.detail(spec, "Registration", registrationNumber.value());
        ParkingOutput.detail(spec, "Vehicle type", vehicleType);

        return 0;
    }
}
