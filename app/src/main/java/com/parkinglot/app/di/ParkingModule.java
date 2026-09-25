package com.parkinglot.app.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.parkinglot.app.application.service.DefaultParkingService;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.domain.policy.NaiveSpotSelectionStrategy;
import com.parkinglot.app.domain.policy.SpotSelectionStrategy;
import com.parkinglot.app.domain.repository.ParkingLotRepository;
import com.parkinglot.app.infrastructure.config.ClockProvider;
import com.parkinglot.app.infrastructure.idgenerator.ReservationIdGenerator;
import com.parkinglot.app.infrastructure.persistence.local.CliStorageManager;
import com.parkinglot.app.infrastructure.persistence.local.LocalParkingLotRepository;

import java.time.Clock;

public class ParkingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Clock.class).toProvider(ClockProvider.class).in(Singleton.class);

        bind(SpotSelectionStrategy.class).to(NaiveSpotSelectionStrategy.class);

        bind(ParkingLotRepository.class).to(LocalParkingLotRepository.class);
    }

    @Provides
    @Singleton
    public ReservationIdGenerator provideReservationIdGenerator() {
        return new ReservationIdGenerator("RES");
    }

    @Provides
    @Singleton
    public CliStorageManager provideCliStorageManager() {
        return new CliStorageManager("parking");
    }
}
