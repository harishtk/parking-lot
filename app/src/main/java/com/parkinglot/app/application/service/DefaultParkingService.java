package com.parkinglot.app.application.service;

import com.google.inject.Inject;
import com.parkinglot.app.application.exception.ParkingLotNotFoundException;
import com.parkinglot.app.application.exception.VehicleNotFoundException;
import com.parkinglot.app.domain.ParkingLotFactory;
import com.parkinglot.app.domain.exception.ParkingSpotUnavailableException;
import com.parkinglot.app.domain.exception.ReservationAlreadyExistsException;
import com.parkinglot.app.domain.exception.TicketAlreadyExistsException;
import com.parkinglot.app.domain.model.*;
import com.parkinglot.app.domain.policy.FeeCalculationStrategy;
import com.parkinglot.app.domain.policy.SpotSelectionStrategy;
import com.parkinglot.app.domain.repository.ParkingLotRepository;
import com.parkinglot.app.domain.valueobject.AllocationId;
import com.parkinglot.app.domain.valueobject.Money;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import com.parkinglot.app.domain.valueobject.TicketId;
import com.parkinglot.app.infrastructure.idgenerator.AllocationIdGenerator;
import com.parkinglot.app.infrastructure.idgenerator.SpotIdGenerator;
import com.parkinglot.app.infrastructure.idgenerator.TicketIdGenerator;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class DefaultParkingService implements ParkingService {

    private final ParkingLotFactory factory;
    private final ParkingLotRepository parkingLotRepository;

    private final TicketIdGenerator ticketIdGenerator;
    private final AllocationIdGenerator allocationIdGenerator;

    private final SpotSelectionStrategy spotSelectionStrategy;
    private final FeeCalculationStrategy feeCalculationStrategy;

    private final Clock clock;

    @Inject
    public DefaultParkingService(ParkingLotFactory factory,
                                 ParkingLotRepository parkingLotRepository,
                                 TicketIdGenerator ticketIdGenerator,
                                 AllocationIdGenerator allocationIdGenerator,
                                 SpotIdGenerator spotIdGenerator,
                                 SpotSelectionStrategy spotSelectionStrategy,
                                 FeeCalculationStrategy feeCalculationStrategy,
                                 Clock clock) {
        this.factory = factory;
        this.parkingLotRepository = parkingLotRepository;
        this.ticketIdGenerator = ticketIdGenerator;
        this.allocationIdGenerator = allocationIdGenerator;
        this.spotSelectionStrategy = spotSelectionStrategy;
        this.feeCalculationStrategy = feeCalculationStrategy;
        this.clock = clock;
    }

    @Override
    public Ticket parkVehicle(RegistrationNumber registrationNumber,
                                        VehicleType vehicleType) {

        Optional<Ticket> existingTicket = findVehicle(registrationNumber);
        if (existingTicket.isPresent()) {
            throw new TicketAlreadyExistsException(existingTicket.get(), registrationNumber);
        }

        String idInput = registrationNumber.value() + "-" + vehicleType.name();
        TicketId ticketId = ticketIdGenerator.next(idInput);
        AllocationId allocationId =  allocationIdGenerator.next(idInput);
        Instant entryTime = clock.instant();

        ParkingLot parkingLot = getParkingLot();

        List<ParkingSpot> freeSpots = parkingLot.findCandidateSpots();
        List<Reservation> reservations = parkingLot.findActiveReservations();

        ParkingSpot spot = spotSelectionStrategy.selectSpot(
                vehicleType,
                registrationNumber,
                freeSpots,
                reservations
        )
                .orElseThrow(ParkingSpotUnavailableException::new);

        Ticket ticket = parkingLot.allocate(
                ticketId,
                allocationId,
                registrationNumber,
                vehicleType,
                spot.id(),
                entryTime
        );

        parkingLotRepository.save(parkingLot);
        return ticket;
    }

    @Override
    public Ticket unparkVehicle(RegistrationNumber registrationNumber) {
        ParkingLot parkingLot = getParkingLot();
        Instant exitTime = clock.instant();

        Ticket ticket = parkingLot.findVehicle(registrationNumber)
                .orElseThrow(() -> new VehicleNotFoundException(registrationNumber));

        Money fee = feeCalculationStrategy.calculate(ticket.vehicleType(), ticket.elapsedDuration(exitTime));

        ticket = parkingLot.release(
                ticket.id(),
                fee,
                exitTime
        );

        parkingLotRepository.save(parkingLot);
        return ticket;
    }

    @Override
    public void createParkingLot(int floors, int carSpotsPerFloor, int bikeSpotsPerFloor) {
        ParkingLot parkingLot =
                factory.create(
                        floors,
                        carSpotsPerFloor,
                        bikeSpotsPerFloor
                );

        parkingLotRepository.save(parkingLot);
    }

    @Override
    public Optional<Ticket> findTicket(TicketId ticketId) {
        ParkingLot lot = getParkingLot();
        return lot.findActiveTicket(ticketId);
    }

    @Override
    public Optional<Ticket> findVehicle(RegistrationNumber registrationNumber) {
        ParkingLot lot = getParkingLot();
        return lot.findVehicle(registrationNumber);
    }

    @Override
    public List<Ticket> findAllTickets() {
        return getParkingLot().findAllTickets();
    }

    @Override
    public List<Ticket> findActiveTickets() {
        return getParkingLot().findActiveTickets();
    }

    @Override
    public ParkingLot getParkingLot() {
        return parkingLotRepository.load()
                .orElseThrow(ParkingLotNotFoundException::new);
    }

    @Override
    public boolean deleteParkingLot() {
        return parkingLotRepository.delete();
    }
}
