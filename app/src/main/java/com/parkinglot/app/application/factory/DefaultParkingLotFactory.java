package com.parkinglot.app.application.factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parkinglot.app.domain.ParkingLotFactory;
import com.parkinglot.app.domain.model.Floor;
import com.parkinglot.app.domain.model.ParkingLot;
import com.parkinglot.app.domain.model.ParkingSpot;
import com.parkinglot.app.domain.valueobject.FloorId;
import com.parkinglot.app.domain.valueobject.SpotId;
import com.parkinglot.app.infrastructure.idgenerator.SpotIdGenerator;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class DefaultParkingLotFactory implements ParkingLotFactory {

    private final SpotIdGenerator spotIdGenerator;

    @Inject
    public DefaultParkingLotFactory(
            SpotIdGenerator spotIdGenerator
    ) {
        this.spotIdGenerator = spotIdGenerator;
    }

    public ParkingLot create(
            int floorCount,
            int carSpotsPerFloor,
            int bikeSpotsPerFloor
    ) {
        validate(
                floorCount,
                carSpotsPerFloor,
                bikeSpotsPerFloor
        );

        Map<FloorId, Floor> floors = new HashMap<>();

        for (int floorNumber = 1; floorNumber <= floorCount; floorNumber++) {

            Floor floor = buildFloor(
                    floorNumber,
                    carSpotsPerFloor,
                    bikeSpotsPerFloor
            );

            floors.put(floor.id(), floor);
        }

        return ParkingLot.initialize(floors);
    }

    private Floor buildFloor(
            int floorNumber,
            int carSpotsPerFloor,
            int bikeSpotsPerFloor
    ) {
        Map<SpotId, ParkingSpot> spots = new HashMap<SpotId, ParkingSpot>();

        createCarSpots(
                floorNumber,
                carSpotsPerFloor,
                spots
        );

        createBikeSpots(
                floorNumber,
                bikeSpotsPerFloor,
                spots
        );

        return Floor.initialize(
                new FloorId(String.valueOf(floorNumber)),
                spots
        );
    }

    private void createCarSpots(
            int floorNumber,
            int count,
            Map<SpotId, ParkingSpot> spots
    ) {
        for (int i = 1; i <= count; i++ ) {

            SpotId spotId = spotIdGenerator.next("F%02d-C%02d".formatted(floorNumber, i));

            spots.put(
                    spotId,
                    ParkingSpot.carSpot(spotId)
            );
        }
    }

    private void createBikeSpots(
            int floorNumber,
            int count,
            Map<SpotId, ParkingSpot> spots
    ) {
        for (int i = 1; i <= count; i++ ) {

            SpotId spotId = spotIdGenerator.next("F%02d-B%02d".formatted(floorNumber, i));

            spots.put(
                    spotId,
                    ParkingSpot.bikeSpot(spotId)
            );
        }
    }

    private static void validate(
            int floorCount,
            int carSpotsPerFloor,
            int bikeSpotsPerFloor
    ) {
        if (floorCount <= 0) {
            throw new IllegalArgumentException("At least one floor is required");
        }

        if (carSpotsPerFloor < 0) {
            throw new IllegalArgumentException("Car spots cannot be negative");
        }

        if (bikeSpotsPerFloor < 0) {
            throw new IllegalArgumentException("Bike spots cannot be negative");
        }

        if (carSpotsPerFloor == 0
                && bikeSpotsPerFloor == 0) {
            throw new IllegalArgumentException("Parking lot must contain at least one parking spot");
        }
    }
}
