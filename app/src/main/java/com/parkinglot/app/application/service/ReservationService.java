package com.parkinglot.app.application.service;

import com.parkinglot.app.domain.model.Reservation;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.ReservationId;

public interface ReservationService {

    Reservation reserve(
            RegistrationNumber registrationNumber,
            VehicleType vehicleType
    );

    Reservation cancel(ReservationId reservationId);

    Reservation getReservationById(ReservationId reservationId);
}
