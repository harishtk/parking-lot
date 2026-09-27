package com.parkinglot.app.domain.model;

public record ParkingLotMetaData(
        int numFloors,
        int numSpots,
        int numTickets,
        int numReservations,
        long numOccupiedSpots
) {
    
}
