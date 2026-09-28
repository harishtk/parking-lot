package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.valueobject.Money;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.SpotId;
import com.parkinglot.app.domain.valueobject.TicketId;

import java.math.BigDecimal;
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

    private Ticket(TicketId ticketId, RegistrationNumber registrationNumber, VehicleType vehicleType, SpotId spotId, Instant entryTime, Instant exitTime, Money fee, TicketStatus status) {
        this.ticketId = ticketId;
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
        this.spotId = spotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.fee = fee;
        this.status = status;
    }

    public Ticket close(Instant exitTime, Money fee) {
        if (this.status == TicketStatus.CLOSED) {
            return this;
        }

        this.exitTime = exitTime;
        this.fee = fee;
        this.status = TicketStatus.CLOSED;
        return this;
    }

    public boolean isActive() {
        return this.status == TicketStatus.ACTIVE;
    }

    public boolean belongsTo(RegistrationNumber registrationNumber) {
        return this.registrationNumber.equals(registrationNumber);
    }

    public Duration elapsedDuration(Instant exitTime) {
        return Duration.between(this.entryTime, exitTime);
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

    public record Snapshot(
            String ticketId,
            String registrationNumber,
            String vehicleType,
            String spotId,
            String entryTime,
            String exitTime,
            String fee,
            String status
    ) {

    }

    public Snapshot toSnapshot() {
        return new Snapshot(
                ticketId.value(),
                registrationNumber.value(),
                vehicleType.name(),
                spotId.id(),
                entryTime.toString(),
                exitTime == null ? "" : exitTime.toString(),
                fee == null ? "0.0" : fee.toString(),
                status.name()
        );
    }

    public Ticket restoreFromSnapshot(Snapshot snapshot) {
        return new Ticket(
                new TicketId(snapshot.ticketId),
                new RegistrationNumber(snapshot.registrationNumber),
                VehicleType.valueOf(snapshot.vehicleType),
                new SpotId(snapshot.spotId),
                Instant.parse(snapshot.entryTime),
                Instant.parse(snapshot.exitTime),
                new Money(BigDecimal.valueOf(Double.parseDouble(snapshot.fee))),
                TicketStatus.valueOf(snapshot.status)
        );
    }
}

