package com.parkinglot.app.cli;

import com.google.inject.Inject;
import com.parkinglot.app.cli.io.ParkingOutput;
import com.parkinglot.app.domain.repository.ParkingLotRepository;
import com.parkinglot.app.infrastructure.persistence.local.CliStorageManager;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "delete-lot",
        aliases = {"delete"},
        description = "Deletes the current parking lot.",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        exitCodeOnInvalidInput = 2,
        exitCodeOnExecutionException = 1
)
public class DeleteParkingLotCommand implements Callable<Integer> {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    private final ParkingLotRepository repository;

    @Inject
    public DeleteParkingLotCommand(CliStorageManager storageManager, ParkingLotRepository repository) {
        this.repository = repository;
    }

    @Override
    public Integer call() throws Exception {
        if (repository.delete()) {
            spec.commandLine().getOut().println(CommandLine.Help.Ansi.AUTO.string("@|bold,green Successfully deleted the parking lot.|@"));
            return 0;
        } else {
            ParkingOutput.printErr(
                    spec,
                    "Failed to delete the parking lot."
            );
            return 1;
        }
    }
}
