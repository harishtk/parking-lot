package com.parkinglot.app.infrastructure.config;

import com.google.inject.Provider;

import java.time.Clock;

public class ClockProvider implements Provider<Clock> {

    @Override
    public Clock get() {
        return Clock.systemUTC();
    }
}
