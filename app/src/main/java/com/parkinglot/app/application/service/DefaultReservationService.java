package com.parkinglot.app.application.service;

import com.google.inject.Inject;
import com.parkinglot.app.application.exception.ParkingLotNotFoundException;
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
        ParkingLot parkingLot = getParkingLot();
        Reservation reservation = parkingLot.createReservation(
                idGenerator.next(registrationNumber.value()),
                registrationNumber,
                vehicleType
        );

        parkingLotRepository.save(parkingLot);
        return reservation;
    }

    public Reservation cancel(ReservationId reservationId) {
        ParkingLot parkingLot = getParkingLot();
        Reservation reservation = parkingLot.cancelReservation(reservationId);
        parkingLotRepository.save(parkingLot);
        return reservation;
    }

    public Reservation getReservationById(ReservationId reservationId) {
        ParkingLot parkingLot = getParkingLot();
        return parkingLot.findReservation(reservationId);
    }

    private ParkingLot getParkingLot() {
        return parkingLotRepository.load()
                .orElseThrow(ParkingLotNotFoundException::new);
    }
}
