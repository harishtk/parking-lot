package com.parkinglot.app.application.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.parkinglot.app.application.service.DefaultParkingService;
import com.parkinglot.app.application.service.ParkingService;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ParkingService.class).to(DefaultParkingService.class).in(Singleton.class);
    }
}
