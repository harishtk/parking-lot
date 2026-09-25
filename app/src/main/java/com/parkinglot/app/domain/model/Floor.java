package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.valueobject.FloorId;
import com.parkinglot.app.domain.valueobject.SpotId;

import java.util.Map;
import java.util.Optional;

public class Floor {

    private final FloorId floorId;

    private final Map<SpotId, ParkingSpot> parkingSpots;

    public Floor(FloorId floorId, Map<SpotId, ParkingSpot> parkingSpots) {
        this.floorId = floorId;
        this.parkingSpots = parkingSpots;
    }

    public Map<SpotId, ParkingSpot> spots() {
        return parkingSpots;
    }

    public void addSpot(ParkingSpot parkingSpot) {
        parkingSpots.put(parkingSpot.id(), parkingSpot);
    }

    public Optional<ParkingSpot> findSpot(SpotId spotId) {
        return Optional.ofNullable(parkingSpots.get(spotId));
    }

    public FloorId id() {
        return floorId;
    }
}
