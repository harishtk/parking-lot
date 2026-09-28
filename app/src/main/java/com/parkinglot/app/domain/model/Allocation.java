package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.valueobject.AllocationId;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.TicketId;

import java.time.Instant;

public class Allocation {

    private AllocationId allocationId;

    private RegistrationNumber registrationNumber;

    private VehicleType vehicleType;

    private TicketId ticketId;

    private Instant allocatedAt;

    private int capacityConsumed;

    /* Required empty constructor for jackson */
    public Allocation() {}

    public Allocation(AllocationId allocationId,
                      RegistrationNumber registrationNumber,
                      VehicleType vehicleType,
                      TicketId ticketId,
                      Instant allocatedAt) {
        this.allocationId = allocationId;
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
        this.ticketId = ticketId;
        this.allocatedAt = allocatedAt;

        this.capacityConsumed = vehicleType.getUnitSize();
    }

    public AllocationId getAllocationId() {
        return allocationId;
    }

    public boolean belongsTo(TicketId ticketId) {
        return this.ticketId.equals(ticketId);
    }

    public int consumesCapacity() {
        return capacityConsumed;
    }

    public record Snapshot(
            AllocationId allocationId,
            RegistrationNumber registrationNumber,
            VehicleType vehicleType,
            TicketId ticketId,
            Instant allocatedAt
    ) {}

    public Snapshot toSnapshot() {
        return new Snapshot(
                allocationId,
                registrationNumber,
                vehicleType,
                ticketId,
                allocatedAt
        );
    }

    public Allocation restoreFromSnapshot(Snapshot snapshot) {
        return new Allocation(
                snapshot.allocationId,
                snapshot.registrationNumber,
                snapshot.vehicleType,
                snapshot.ticketId,
                snapshot.allocatedAt
        );
    }
}

