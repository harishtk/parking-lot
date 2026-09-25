package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.valueobject.FloorId;
import com.parkinglot.app.domain.valueobject.SpotId;

import java.util.HashMap;
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

    public static Floor create(
            int floorNumber,
            int carSpotsPerFloor,
            int bikeSpotsPerFloor
    ) {
        Map<SpotId, ParkingSpot> spots = new HashMap<SpotId, ParkingSpot>();

        // Car spots
        for (int i = 1; i <= carSpotsPerFloor; i++ ) {

            SpotId spotId = new SpotId("F%02d-C%02d".formatted(floorNumber, i));

            spots.put(
                    spotId,
                    ParkingSpot.carSpot(spotId)
            );
        }

        // Bike spots
        for (int i = 1; i <= bikeSpotsPerFloor; i++ ) {

            SpotId spotId = new SpotId("F%02d-B%02d".formatted(floorNumber, i));

            spots.put(
                    spotId,
                    ParkingSpot.bikeSpot(spotId)
            );
        }

        return new Floor(
                new FloorId(String.valueOf(floorNumber)),
                spots
        );
    }
}
