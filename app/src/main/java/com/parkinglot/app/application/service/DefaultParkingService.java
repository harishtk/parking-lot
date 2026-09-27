package com.parkinglot.app.application.service;

import com.google.inject.Inject;
import com.parkinglot.app.application.exception.ParkingLotNotFoundException;
import com.parkinglot.app.application.exception.VehicleNotFoundException;
import com.parkinglot.app.domain.ParkingLotFactory;
import com.parkinglot.app.domain.exception.ParkingSpotUnavailableException;
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

import java.math.BigDecimal;
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
    public Optional<Ticket> parkVehicle(RegistrationNumber registrationNumber,
                                        VehicleType vehicleType) {
        String idInput = registrationNumber.value() + "-" + vehicleType.name();
        TicketId ticketId = ticketIdGenerator.next(idInput);
        AllocationId allocationId =  allocationIdGenerator.next(idInput);
        Instant entryTime = clock.instant();

        ParkingLot parkingLot = parkingLotRepository.load()
                .orElseThrow(ParkingLotNotFoundException::new);

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

        return Optional.of(ticket);
    }

    @Override
    public Optional<Ticket> unparkVehicle(RegistrationNumber registrationNumber) {
        ParkingLot parkingLot = parkingLotRepository.load()
                .orElseThrow(ParkingLotNotFoundException::new);
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
        return Optional.of(ticket);
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
        return parkingLotRepository.load()
                .flatMap(lot -> lot.findActiveTicket(ticketId));
    }

    @Override
    public Optional<Ticket> findVehicle(RegistrationNumber registrationNumber) {
        return parkingLotRepository.load()
                .flatMap(lot -> lot.findVehicle(registrationNumber));
    }

    @Override
    public Optional<ParkingLot> getParkingLot() {
        return parkingLotRepository.load();
    }
}
