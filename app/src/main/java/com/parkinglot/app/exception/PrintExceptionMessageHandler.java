package com.parkinglot.app.exception;

import com.parkinglot.app.application.exception.ParkingLotNotFoundException;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;
import com.parkinglot.app.cli.io.ParkingOutput;

public class PrintExceptionMessageHandler implements CommandLine.IExecutionExceptionHandler {

    @Override
    public int handleExecutionException(Exception ex,
                                        CommandLine cmd,
                                        ParseResult parseResult) throws Exception {
        ParkingOutput.printErr(cmd.getCommandSpec(),
                ex.getMessage() == null ? "The command could not be completed." : ex.getMessage());

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}
