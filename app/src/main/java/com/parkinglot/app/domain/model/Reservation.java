package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.ReservationId;

import java.time.Instant;

public class Reservation {

    private ReservationId reservationId;

    private RegistrationNumber registrationNumber;

    private VehicleType vehicleType;

    private Instant createdAt;

    private ReservationStatus status = ReservationStatus.ACTIVE;

    /* Required empty constructor for jackson */
    public Reservation() {}

    public Reservation(ReservationId reservationId,
                       RegistrationNumber registrationNumber,
                       VehicleType vehicleType,
                       Instant createdAt) {
        this.reservationId = reservationId;
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
        this.createdAt = createdAt;
    }

    public Reservation cancel() {
        if (this.status == ReservationStatus.CLOSED) {
            return this;
        }

        this.status = ReservationStatus.CLOSED;
        return this;
    }

    public boolean isActive() {
        return this.status == ReservationStatus.ACTIVE;
    }

    public boolean belongsTo(RegistrationNumber registrationNumber) {
        return this.registrationNumber.equals(registrationNumber);
    }

    public ReservationId id() {
        return reservationId;
    }

    public RegistrationNumber getRegistrationNumber() {
        return registrationNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}

