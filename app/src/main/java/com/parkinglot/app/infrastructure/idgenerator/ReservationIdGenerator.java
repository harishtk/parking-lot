package com.parkinglot.app.infrastructure.idgenerator;

import com.parkinglot.app.domain.IdGenerator;
import com.parkinglot.app.domain.valueobject.ReservationId;

import java.time.Instant;

public record ReservationIdGenerator(String prefix) implements IdGenerator<ReservationId, String> {

    @Override
    public ReservationId next(String input) {
        return new ReservationId("%s-%s-%s".formatted(prefix, input, Instant.now().toString()));
    }
}
