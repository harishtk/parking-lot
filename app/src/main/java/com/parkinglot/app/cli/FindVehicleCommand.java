package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.domain.model.Ticket;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Optional;
import java.util.concurrent.Callable;

@Command(
        name = "find",
        description = "Find vehicle"
)
public class FindVehicleCommand implements Callable<Integer> {

    @Option(names = {"-v", "--vehicle"}, description = "Vehicle Registration Number", required = true)
    private String vehicleNumber;

    private final ParkingService parkingService;

    @Inject
    public FindVehicleCommand(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {
        RegistrationNumber registrationNumber = new RegistrationNumber(vehicleNumber);

        parkingService.findVehicle(registrationNumber)
                .ifPresentOrElse(ticket -> {
                            System.out.println("Vehicle found: " + ticket);
                        },
                        () -> System.out.println("Vehicle not found")
                );

        return 0;
    }
}
