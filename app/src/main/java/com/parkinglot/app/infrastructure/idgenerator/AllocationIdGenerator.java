package com.parkinglot.app.infrastructure.idgenerator;

import com.parkinglot.app.domain.IdGenerator;
import com.parkinglot.app.domain.valueobject.AllocationId;

import java.time.Instant;

public record AllocationIdGenerator(String prefix) implements IdGenerator<AllocationId, String> {

    @Override
    public AllocationId next(String input) {
        return new AllocationId("%s-%s-%s".formatted(prefix, input, Instant.now().toString()));
    }
}
