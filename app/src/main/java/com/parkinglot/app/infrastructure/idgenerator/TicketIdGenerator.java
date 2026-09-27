package com.parkinglot.app.infrastructure.idgenerator;

import com.parkinglot.app.domain.IdGenerator;
import com.parkinglot.app.domain.valueobject.AllocationId;
import com.parkinglot.app.domain.valueobject.SpotId;
import com.parkinglot.app.domain.valueobject.TicketId;

import java.time.Instant;

public record TicketIdGenerator(String prefix) implements IdGenerator<TicketId, String> {

    @Override
    public TicketId next(String input) {
        return new TicketId("%s-%s-%s".formatted(prefix, input, Instant.now().toString()));
    }
}
