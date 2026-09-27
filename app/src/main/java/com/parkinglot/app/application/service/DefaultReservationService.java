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

    private final ParkingLotRepository parkingLotRepository;

    private final ReservationIdGenerator idGenerator;

    @Inject
    public DefaultReservationService(
            ParkingLotRepository parkingLotRepository,
            ReservationIdGenerator idGenerator
    ) {
        this.parkingLotRepository = parkingLotRepository;
        this.idGenerator = idGenerator;
    }

    public Reservation reserve(
            RegistrationNumber registrationNumber,
            VehicleType vehicleType
    ) {
        ParkingLot parkingLot = parkingLotRepository.load()
                .orElseThrow(() -> new RuntimeException("Unable to load parking lot"));

        Reservation reservation = parkingLot.createReservation(
                idGenerator.next(registrationNumber.value()),
                registrationNumber,
                vehicleType
        );

        parkingLotRepository.save(parkingLot);
        return reservation;
    }

    public Reservation cancel(ReservationId reservationId) {
        ParkingLot parkingLot = parkingLotRepository.load()
                .orElseThrow(() -> new RuntimeException("Unable to load parking lot"));
        return parkingLot.cancelReservation(reservationId);
    }

    public Reservation getReservationById(ReservationId reservationId) {
        ParkingLot parkingLot = parkingLotRepository.load()
                .orElseThrow(() -> new RuntimeException("Unable to load parking lot"));
        return parkingLot.findReservation(reservationId);
    }
}
