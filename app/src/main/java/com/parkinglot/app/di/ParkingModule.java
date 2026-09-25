package com.parkinglot.app.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.parkinglot.app.domain.policy.NaiveSpotSelectionStrategy;
import com.parkinglot.app.domain.policy.SpotSelectionStrategy;
import com.parkinglot.app.infrastructure.config.ClockProvider;
import com.parkinglot.app.infrastructure.idgenerator.ReservationIdGenerator;

import java.time.Clock;

public class ParkingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Clock.class).toProvider(ClockProvider.class).in(Singleton.class);

        bind(SpotSelectionStrategy.class).to(NaiveSpotSelectionStrategy.class);
    }

    @Provides
    @Singleton
    public ReservationIdGenerator provideReservationIdGenerator() {
        return new ReservationIdGenerator("RES");
    }
}
