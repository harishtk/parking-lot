package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.valueobject.Money;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.SpotId;
import com.parkinglot.app.domain.valueobject.TicketId;

import java.time.Duration;
import java.time.Instant;

public class Ticket {

    private TicketId ticketId;

    private RegistrationNumber registrationNumber;

    private VehicleType vehicleType;

    private SpotId spotId;

    private Instant entryTime;

    private Instant exitTime;

    private Money fee;

    private TicketStatus status = TicketStatus.ACTIVE;

    /* Required empty constructor for jackson */
    public Ticket() {}

    public Ticket(TicketId ticketId,
                  RegistrationNumber registrationNumber,
                  VehicleType vehicleType,
                  SpotId spotId,
                  Instant entryTime) {
        this.ticketId = ticketId;
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
        this.spotId = spotId;
        this.entryTime = entryTime;
    }

    public Ticket close(
            Instant exitTime,
            Money fee
    ) {
        if (this.status == TicketStatus.CLOSED) {
            return this;
        }

        this.exitTime = exitTime;
        this.fee = fee;
        return this;
    }

    public boolean isActive() {
        return this.status == TicketStatus.ACTIVE;
    }

    public boolean belongsTo(RegistrationNumber registrationNumber) {
        return this.registrationNumber.equals(registrationNumber);
    }

    public Duration duration() {
        if (exitTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(this.entryTime, this.exitTime);
    }

    public TicketId id() {
        return this.ticketId;
    }

    public SpotId spotId() {
        return this.spotId;
    }

    public VehicleType vehicleType() {
        return this.vehicleType;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", registrationNumber=" + registrationNumber.value() +
                ", vehicleType=" + vehicleType +
                ", spotId=" + spotId.id() +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                ", fee=" + fee +
                ", status=" + status +
                '}';
    }
}

