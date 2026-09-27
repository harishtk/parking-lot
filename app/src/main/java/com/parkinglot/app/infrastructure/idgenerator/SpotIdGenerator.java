package com.parkinglot.app.infrastructure.idgenerator;

import com.parkinglot.app.domain.IdGenerator;
import com.parkinglot.app.domain.valueobject.SpotId;

import java.time.Instant;

public record SpotIdGenerator(String prefix) implements IdGenerator<SpotId, String> {

    @Override
    public SpotId next(String input) {
        return new SpotId("%s-%s".formatted(prefix, input));
    }
}
