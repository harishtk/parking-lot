package com.parkinglot.app.domain.valueobject;

import java.io.Serializable;

public record TicketId(String id) implements Serializable {

    public TicketId {
        if (null == id || id.isEmpty()) {
            throw new IllegalArgumentException("TicketId's value cannot be null or empty.");
        }
    }
}


