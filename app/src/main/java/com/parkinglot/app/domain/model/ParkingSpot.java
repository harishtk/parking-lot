package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.exception.AllocationNotFoundException;
import com.parkinglot.app.domain.valueobject.AllocationId;
import com.parkinglot.app.domain.valueobject.SpotId;
import com.parkinglot.app.domain.valueobject.TicketId;

import java.util.Map;
import java.util.Optional;

public class ParkingSpot {

    private final SpotId spotId;

    private final SpotType spotType;

    private final int totalCapacity;

    private final Map<AllocationId, Allocation> allocations;

    public ParkingSpot(SpotId spotId, SpotType spotType, int capacity, Map<AllocationId, Allocation> allocations) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.totalCapacity = capacity;
        this.allocations = allocations;
    }

    public Allocation release(TicketId ticketId) {
        final Allocation allocation = allocations.entrySet()
                .stream()
                .filter(entry -> entry.getValue().belongsTo(ticketId))
                .findFirst()
                .orElseThrow(() ->
                        new AllocationNotFoundException(ticketId))
                .getValue();

        allocations.remove(allocation.getAllocationId());

        return allocation;
    }

    public int remainingCapacity() {
        return totalCapacity - occupiedCapacity();
    }

    public boolean canAccommodate(int newCapacity) {
        return this.totalCapacity <= (remainingCapacity() + newCapacity);
    }

    public boolean isFull() {
        return totalCapacity == remainingCapacity();
    }

    public boolean isEmpty() {
        return occupiedCapacity() == 0;
    }

    private int occupiedCapacity() {
        return allocations.values()
                .stream()
                .mapToInt(Allocation::consumesCapacity)
                .sum();
    }

    public Optional<Allocation> getAllocation(AllocationId allocationId) {
        return Optional.ofNullable(allocations.get(allocationId));
    }

    public void addAllocation(Allocation allocation) {
        allocations.put(allocation.getAllocationId(), allocation);
    }

    public void removeAllocation(AllocationId allocationId) {
        allocations.remove(allocationId);
    }

    public Map<AllocationId, Allocation> allocations() {
        return allocations;
    }

    public SpotId id() {
        return spotId;
    }

    public SpotType spotType() {
        return spotType;
    }
}

