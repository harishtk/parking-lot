package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.domain.model.Ticket;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Optional;
import java.util.concurrent.Callable;

@Command(
        name = "park",
        description = "Park vehicle"
)
public class ParkVehicleCommand implements Callable<Integer> {

    @Option(names = {"-v", "--vehicle"}, description = "Vehicle registration number", required = true)
    private String vehicleNumber;

    @Option(names = {"-t", "--type"}, description = "Vehicle type (car, bike)", required = true)
    private String vehicleType;

    private final ParkingService parkingService;

    @Inject
    public ParkVehicleCommand(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {
        RegistrationNumber registrationNumber = new RegistrationNumber(vehicleNumber);
        VehicleType vehicleTypeEnum = VehicleType.valueOf(vehicleType.toUpperCase());

        Optional<Ticket> ticket = parkingService.parkVehicle(
                registrationNumber,
                vehicleTypeEnum
        );
        if (ticket.isPresent()) {
            System.out.println("Vehicle parked successfully: ticket " + ticket.get());
            return 0;
        } else {
            System.out.println("Unable to park vehicle.");
            return 1;
        }
    }
}
