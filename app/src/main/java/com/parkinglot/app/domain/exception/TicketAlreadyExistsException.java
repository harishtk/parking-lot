package com.parkinglot.app.domain.exception;

import com.parkinglot.app.domain.model.Ticket;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;

public class TicketAlreadyExistsException extends RuntimeException {
    public TicketAlreadyExistsException(RegistrationNumber registrationNumber) {
        super("Ticket with registration " + registrationNumber.value() + " already exists");
    }

    public TicketAlreadyExistsException(Ticket ticket, RegistrationNumber registrationNumber) {
        super("Ticket with registration %s already exists ticket: %s".formatted(registrationNumber.value(), ticket.id().value()));
    }
}
