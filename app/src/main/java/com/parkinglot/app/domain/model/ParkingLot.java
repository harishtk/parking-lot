package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.exception.ParkingSpotNotFoundException;
import com.parkinglot.app.domain.exception.ReservationNotFoundException;
import com.parkinglot.app.domain.exception.TicketNotFoundException;
import com.parkinglot.app.domain.valueobject.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParkingLot {

    private final Map<FloorId, Floor> floors;

    private final Map<TicketId, Ticket> tickets;

    private final Map<ReservationId, Reservation> reservations;

    private final Map<RegistrationNumber, TicketId> vehicleTicketIndex;

    public ParkingLot(Map<FloorId, Floor> floors,
                      Map<TicketId, Ticket> tickets,
                      Map<ReservationId, Reservation> reservations,
                      Map<RegistrationNumber, TicketId> vehicleTicketIndex) {
        this.floors = floors;
        this.tickets = tickets;
        this.reservations = reservations;
        this.vehicleTicketIndex = vehicleTicketIndex;
    }

    private ParkingLot(Map<FloorId, Floor> floors) {
        this(floors, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public Ticket allocate(
            TicketId ticketId,
            AllocationId allocationId,
            RegistrationNumber registrationNumber,
            VehicleType vehicleType,
            SpotId spotId,
            Instant entryTime
    ) {
        Ticket newTicket = new Ticket(
                ticketId,
                registrationNumber,
                vehicleType,
                spotId,
                entryTime
        );

        Allocation allocation = new Allocation(
                allocationId,
                registrationNumber,
                vehicleType,
                ticketId,
                entryTime
        );

        ParkingSpot spot = findSpot(spotId);
        spot.addAllocation(allocation);

        tickets.put(ticketId, newTicket);
        vehicleTicketIndex.put(registrationNumber, ticketId);

        reservations.values()
                .stream()
                .filter(r -> r.belongsTo(registrationNumber))
                .findFirst()
                .ifPresent(reservation -> {
                    reservations.put(reservation.id(), reservation.cancel());
                });

        return newTicket;
    }

    public Ticket release(
            TicketId ticketId,
            Money fee,
            Instant time
    ) {
        Ticket ticket = Optional.ofNullable(tickets.get(ticketId))
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        ParkingSpot spot = findSpot(ticket.spotId());
        spot.release(ticketId);

        return ticket.close(time, fee);
    }

    public Reservation createReservation(
            ReservationId reservationId,
            RegistrationNumber registrationNumber,
            VehicleType vehicleType
    ) {
        final Reservation reservation = new Reservation(
                reservationId,
                registrationNumber,
                vehicleType,
                Instant.now()
        );

        reservations.put(reservationId, reservation);

        return reservation;
    }

    public Reservation cancelReservation(ReservationId reservationId) {
        final Reservation reservation = Optional.ofNullable(reservations.get(reservationId))
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        Reservation cancelled = reservation.cancel();
        reservations.put(reservationId, cancelled);

        return cancelled;
    }

    public Reservation findReservation(ReservationId reservationId) {
        return Optional.ofNullable(reservations.get(reservationId))
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }

    public ParkingSpot findSpot(SpotId spotId) {
        for (Floor floor : floors.values()) {
            Optional<ParkingSpot> parkingSpot = floor.findSpot(spotId);

            if (parkingSpot.isPresent()) {
                return parkingSpot.get();
            }
        }

        throw new ParkingSpotNotFoundException(spotId);
    }

    public boolean hasActiveTicket(TicketId ticketId) {
        return tickets.containsKey(ticketId);
    }

    public Ticket findActiveTicket(TicketId ticketId) {
        return Optional.ofNullable(tickets.get(ticketId))
                .orElseThrow(() ->
                        new TicketNotFoundException(ticketId));
    }

    public static ParkingLot create(
            int floorCount,
            int carSpotsPerFloor,
            int bikeSpotsPerFloor
    ) {
        Map<FloorId, Floor> floors = new HashMap<>();

        for (int floorNumber = 1; floorNumber <= floorCount; floorNumber++) {

            Floor floor = Floor.create(
                    floorNumber,
                    carSpotsPerFloor,
                    bikeSpotsPerFloor
            );

            floors.put(floor.id(), floor);
        }

        return new ParkingLot(floors);
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
