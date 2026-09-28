package com.parkinglot.app.cli.io;

import com.parkinglot.app.domain.model.Ticket;
import picocli.CommandLine.Model.CommandSpec;

public final class ParkingOutput {

    private ParkingOutput() {
    }

    public static void success(CommandSpec spec, String message) {
        spec.commandLine().getOut().printf("%s  %s%n",
                spec.commandLine().getColorScheme().ansi().string("@|bold,green OK|@"), message);
    }

    public static void heading(CommandSpec spec, String heading) {
        spec.commandLine().getOut().println(spec.commandLine().getColorScheme().commandText(heading));
    }

    public static void detail(CommandSpec spec, String label, Object value) {
        spec.commandLine().getOut().printf("  %-20s %s%n", label, value);
    }

    public static void printTicket(CommandSpec spec,
                                   Ticket ticket) {
        detail(spec, "Ticket", ticket.id().value());
        detail(spec, "Spot", ticket.spotId().id());
        detail(spec, "Vehicle type", ticket.vehicleType());
    }

    public static void printErr(CommandSpec spec, String message) {
        var out = spec.commandLine().getErr();

        out.printf("%s  %s%n", spec.commandLine().getColorScheme().ansi().string("@|bold,red ERROR|@"), message);
    }
}
