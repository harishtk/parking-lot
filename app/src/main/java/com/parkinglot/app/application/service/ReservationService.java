package com.parkinglot.app.application.service;

import com.parkinglot.app.domain.model.Reservation;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.ReservationId;

public interface ReservationService {

    public Reservation reserve(
            RegistrationNumber registrationNumber,
            VehicleType vehicleType
    );

    public Reservation cancel(ReservationId reservationId);

    public Reservation getReservationById(ReservationId reservationId);
}
