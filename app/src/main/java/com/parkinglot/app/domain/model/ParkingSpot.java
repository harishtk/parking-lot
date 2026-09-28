package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.exception.AllocationNotFoundException;
import com.parkinglot.app.domain.valueobject.AllocationId;
import com.parkinglot.app.domain.valueobject.SpotId;
import com.parkinglot.app.domain.valueobject.TicketId;

import java.util.Map;
import java.util.Optional;

public class ParkingSpot {

    private SpotId spotId;

    private SpotType spotType;

    private int totalCapacity;

    private Map<AllocationId, Allocation> allocations;

    /* Required empty constructor for jackson */
    public ParkingSpot() {}

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
        return remainingCapacity() >= newCapacity;
    }

    public boolean isFull() {
        return totalCapacity == occupiedCapacity();
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

    public static ParkingSpot carSpot(
            SpotId spotId
    ) {
        return new ParkingSpot(
                spotId,
                SpotType.CAR,
                SpotType.CAR.getUnitSize(),
                Map.of()
        );
    }

    public static ParkingSpot bikeSpot(
            SpotId spotId
    ) {
        return new ParkingSpot(
                spotId,
                SpotType.BIKE,
                SpotType.BIKE.getUnitSize(),
                Map.of()
        );
    }
}

