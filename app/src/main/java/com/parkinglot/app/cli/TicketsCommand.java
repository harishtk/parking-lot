package com.parkinglot.app.cli;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import com.google.inject.Inject;
import com.parkinglot.app.application.service.ParkingService;
import com.parkinglot.app.cli.io.ParkingOutput;
import com.parkinglot.app.domain.model.Ticket;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "tickets",
        description = "Ticket information.",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class TicketsCommand implements Callable<Integer> {

    @Option(names = {"-a", "--show-all"},
            required = false,
            description = "Show all tickets.",
            defaultValue = "false"
    )
    private boolean showAll;

    @Spec
    private CommandSpec spec;

    private final ParkingService parkingService;

    @Inject
    public TicketsCommand(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public Integer call() throws Exception {
        List<Ticket.Snapshot> tickets;

        if (showAll) {
            tickets = parkingService.findAllTickets()
                    .stream()
                    .map(Ticket::toSnapshot)
                    .sorted(Comparator.comparing(Ticket.Snapshot::ticketId))
                    .toList();
        } else {
            tickets = parkingService.findActiveTickets()
                    .stream()
                    .map(Ticket::toSnapshot)
                    .sorted(Comparator.comparing(Ticket.Snapshot::ticketId))
                    .toList();
        }
        var out = spec.commandLine().getOut();

        out.println();
        ParkingOutput.heading(spec, "Tickets");
        if (tickets.isEmpty()) {
            out.println("  No tickets to display.");
            return 0;
        }

        List<ColumnData<Ticket.Snapshot>> columns = Arrays.asList(
                new Column().header("Id").with(Ticket.Snapshot::ticketId),
                new Column().header("Registration").with(Ticket.Snapshot::registrationNumber),
                new Column().header("Type").with(Ticket.Snapshot::vehicleType),
                new Column().header("Spot").with(Ticket.Snapshot::spotId),
                new Column().header("Entry time").with(Ticket.Snapshot::entryTime),
                new Column().header("Exit time").with(Ticket.Snapshot::exitTime),
                new Column().header("Fee").with(Ticket.Snapshot::fee),
                new Column().header("Status").with(Ticket.Snapshot::status)
        );

        out.println(AsciiTable.getTable(tickets, columns));

        return 0;
    }
}
