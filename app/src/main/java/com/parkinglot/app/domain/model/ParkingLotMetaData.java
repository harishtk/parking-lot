package com.parkinglot.app.domain.model;

public record ParkingLotMetaData(
        int numFloors,
        int numSpots,
        int numTickets,
        long numReservations,
        long numOccupiedSpots
) {
    
}
