package com.parkinglot.app.application.service;

import com.parkinglot.app.domain.model.ParkingLot;
import com.parkinglot.app.domain.model.Ticket;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.TicketId;

import java.util.Optional;

public interface ParkingService {

    Optional<Ticket> parkVehicle(
            RegistrationNumber registrationNumber,
            VehicleType vehicleType
    );

    Optional<Ticket> unparkVehicle(
            RegistrationNumber registrationNumber
    );

    void createParkingLot(
            int floors,
            int carSpotsPerFloor,
            int bikeSpotsPerFloor
    );

    Optional<Ticket> findTicket(TicketId ticketId);

    Optional<Ticket> findVehicle(RegistrationNumber registrationNumber);

    Optional<ParkingLot> getParkingLot();
}
