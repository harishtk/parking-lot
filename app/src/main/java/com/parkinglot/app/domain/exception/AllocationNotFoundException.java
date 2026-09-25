package com.parkinglot.app.domain.exception;

import com.parkinglot.app.domain.valueobject.TicketId;

public class AllocationNotFoundException extends RuntimeException {
    public AllocationNotFoundException(TicketId ticketId) {
        super("No allocation found for ticket id " + ticketId);
    }
}
