package com.parkinglot.app.infrastructure.idgenerator;

import com.parkinglot.app.domain.IdGenerator;

import java.time.Instant;

public record ReservationIdGenerator(String prefix) implements IdGenerator<String, String> {

    @Override
    public String next(String input) {
        return "%s-%s-%s".formatted(prefix, input, Instant.now().toString());
    }
}
