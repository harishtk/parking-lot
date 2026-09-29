package com.parkinglot.app.domain.exception;

import com.parkinglot.app.domain.valueobject.AllocationId;
import com.parkinglot.app.domain.valueobject.TicketId;

public class AllocationNotFoundException extends RuntimeException {
    public AllocationNotFoundException(TicketId ticketId) {
        super("No allocation found for ticket value " + ticketId);
    }

    public AllocationNotFoundException(AllocationId allocationId) {
        super("No allocation found for allocation id " + allocationId);
    }
}
