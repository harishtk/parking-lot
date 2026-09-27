package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ReservationService;
import com.parkinglot.app.domain.model.Reservation;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(
        name = "reserve",
        description = "Reserves a spot for given vehicle registration number"
)
public class ReserveSpotCommand implements Callable<Integer> {

    @Option(names = {"-v", "--vehicle"}, description = "Vehicle registration number", required = true)
    private String vehicleNumber;

    @Option(names = {"-t", "--type"}, description = "Vehicle type (car, bike)", required = true)
    private String vehicleType;

    private final ReservationService reservationService;

    @Inject
    public ReserveSpotCommand(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public Integer call() throws Exception {
        RegistrationNumber registrationNumber = new RegistrationNumber(vehicleNumber);
        VehicleType vehicleType1 = VehicleType.valueOf(vehicleType.toUpperCase());

        Reservation reservation = reservationService
                .reserve(
                        registrationNumber,
                        vehicleType1
                );
        System.out.println("Vehicle reserved: " + reservation.id().value());

        return 0;
    }
}
