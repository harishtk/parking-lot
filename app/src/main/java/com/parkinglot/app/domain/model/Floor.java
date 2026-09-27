package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.valueobject.FloorId;
import com.parkinglot.app.domain.valueobject.SpotId;

import java.util.*;

public class Floor {

    private FloorId floorId;

    private Map<SpotId, ParkingSpot> parkingSpots;

    /* Required empty constructor for jackson */
    public Floor() {}

    public Floor(FloorId floorId, Map<SpotId, ParkingSpot> parkingSpots) {
        this.floorId = floorId;
        this.parkingSpots = parkingSpots;
    }

    public static Floor initialize(
            FloorId floorId,
            Map<SpotId, ParkingSpot> parkingSpots
    ) {
        return new Floor(floorId, parkingSpots);
    }

    public List<ParkingSpot> spots() {
        return new ArrayList<>(parkingSpots.values());
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
