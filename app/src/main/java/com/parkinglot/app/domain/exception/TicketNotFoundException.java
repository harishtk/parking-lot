package com.parkinglot.app.domain.exception;

import com.parkinglot.app.domain.valueobject.TicketId;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(TicketId ticketId) {
        super("Ticket with id " + ticketId + " not found");
    }
}
