package com.parkinglot.app.cli.io;

import com.parkinglot.app.domain.model.Ticket;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

public final class ParkingOutput {

    private  ParkingOutput() {
    }

    public static void printTicket(CommandSpec spec,
                                   Ticket ticket) {
        var out = spec.commandLine().getOut();

        out.printf("  %14s %s%n", "Ticket:", ticket.id().value());
        out.printf("  %14s %s%n", "Spot:", ticket.spotId().id());
    }

    public static void printErr(CommandSpec spec, String message) {
        var out = spec.commandLine().getErr();

        out.println(CommandLine.Help.Ansi.AUTO.string(message));
    }
}
