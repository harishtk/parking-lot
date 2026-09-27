package com.parkinglot.app.domain.model;

import com.parkinglot.app.domain.exception.ParkingSpotNotFoundException;
import com.parkinglot.app.domain.exception.ReservationNotFoundException;
import com.parkinglot.app.domain.exception.TicketNotFoundException;
import com.parkinglot.app.domain.valueobject.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ParkingLot {

    private Map<FloorId, Floor> floors;

    private Map<TicketId, Ticket> tickets;

    private Map<ReservationId, Reservation> reservations;

    private Map<RegistrationNumber, TicketId> vehicleTicketIndex;

    /* Required empty constructor for jackson */
    public ParkingLot() {}

    public ParkingLot(Map<FloorId, Floor> floors,
                      Map<TicketId, Ticket> tickets,
                      Map<ReservationId, Reservation> reservations,
                      Map<RegistrationNumber, TicketId> vehicleTicketIndex) {
        this.floors = floors;
        this.tickets = tickets;
        this.reservations = reservations;
        this.vehicleTicketIndex = vehicleTicketIndex;
    }

    public static ParkingLot initialize(Map<FloorId, Floor> floors) {
        return new ParkingLot(floors);
    }

    public ParkingLot(Map<FloorId, Floor> floors) {
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

    public List<Reservation> findActiveReservations() {
        return reservations.values().stream()
                .filter(Reservation::isActive)
                .toList();
    }

    public List<ParkingSpot> findCandidateSpots() {
        return floors.values().stream()
                .map(Floor::spots)
                .flatMap(List::stream)
                .toList();
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

    public Optional<Ticket> findActiveTicket(TicketId ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public Optional<Ticket> findVehicle(RegistrationNumber registrationNumber) {
        return Optional.ofNullable(vehicleTicketIndex.get(registrationNumber))
                .flatMap(this::findActiveTicket);
    }

    public ParkingLotMetaData describe() {
        int numFloors = floors.size();
        int numSpots = findCandidateSpots().size();
        int numTickets = tickets.size();
        int numReservations = reservations.size();
        long numOccupiedSpots = Math.toIntExact(floors.values().stream()
                .map(Floor::spots)
                .flatMap(List::stream)
                .filter(spot -> !spot.isEmpty())
                .count());

        return new  ParkingLotMetaData(
                numFloors,
                numSpots,
                numTickets,
                numReservations,
                numOccupiedSpots
        );
    }
}