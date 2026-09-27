package com.parkinglot.app.application.service;

import com.google.inject.Inject;
import com.parkinglot.app.domain.model.ParkingLot;
import com.parkinglot.app.domain.model.Reservation;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.repository.ParkingLotRepository;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.ReservationId;
import com.parkinglot.app.infrastructure.idgenerator.ReservationIdGenerator;

public class DefaultReservationService implements ReservationService {

    private final ReservationIdGenerator idGenerator;

    private final ParkingLot parkingLot;

    @Inject
    public DefaultReservationService(
            ParkingLotRepository parkingLotRepository,
            ReservationIdGenerator idGenerator
    ) {

        this.parkingLot = parkingLotRepository.load()
                .orElseThrow(() -> new RuntimeException("Unable to load parking lot"));
        this.idGenerator = idGenerator;
    }

    public Reservation reserve(
            RegistrationNumber registrationNumber,
            VehicleType vehicleType
    ) {
        return parkingLot.createReservation(
                idGenerator.next(registrationNumber.value()),
                registrationNumber,
                vehicleType
        );
    }

    public Reservation cancel(ReservationId reservationId) {
        return parkingLot.cancelReservation(reservationId);
    }

    public Reservation getReservationById(ReservationId reservationId) {
        return parkingLot.findReservation(reservationId);
    }
}
