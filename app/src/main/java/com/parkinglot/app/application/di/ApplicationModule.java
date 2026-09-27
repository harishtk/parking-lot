package com.parkinglot.app.application.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.parkinglot.app.application.factory.DefaultParkingLotFactory;
import com.parkinglot.app.application.service.DefaultParkingService;
import com.parkinglot.app.application.service.DefaultReservationService;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.application.service.ReservationService;
import com.parkinglot.app.domain.ParkingLotFactory;
import com.parkinglot.app.infrastructure.idgenerator.AllocationIdGenerator;
import com.parkinglot.app.infrastructure.idgenerator.ReservationIdGenerator;
import com.parkinglot.app.infrastructure.idgenerator.SpotIdGenerator;
import com.parkinglot.app.infrastructure.idgenerator.TicketIdGenerator;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ParkingLotFactory.class).to(DefaultParkingLotFactory.class).in(Singleton.class);

        bind(ParkingService.class).to(DefaultParkingService.class).in(Singleton.class);
        bind(ReservationService.class).to(DefaultReservationService.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public ReservationIdGenerator provideReservationIdGenerator() {
        return new ReservationIdGenerator("RES");
    }

    @Provides
    @Singleton
    public TicketIdGenerator provideTicketIdGenerator() {
        return new TicketIdGenerator("TIC");
    }

    @Provides
    @Singleton
    public AllocationIdGenerator provideAllocationIdGenerator() {
        return new AllocationIdGenerator("AL");
    }

    @Provides
    @Singleton
    public SpotIdGenerator  provideSpotIdGenerator() {
        return new SpotIdGenerator("SP");
    }
}
