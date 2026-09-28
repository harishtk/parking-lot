package com.parkinglot.app.exception;

import picocli.CommandLine;
import com.parkinglot.app.cli.io.ParkingOutput;

import java.io.PrintWriter;

public class ShortErrorMessageHandler implements CommandLine.IParameterExceptionHandler {
    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] args) throws Exception {
        CommandLine cmd = ex.getCommandLine();
        PrintWriter err = cmd.getErr();

        // if tracing at DEBUG level, show the location of the issue
        if ("DEBUG".equalsIgnoreCase(System.getProperty("picocli.trace"))) {
            err.println(cmd.getColorScheme().stackTraceText(ex));
        }

        ParkingOutput.printErr(cmd.getCommandSpec(), ex.getMessage());
        CommandLine.UnmatchedArgumentException.printSuggestions(ex, err);
        err.println();

        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        err.printf("Try '%s --help' for more information.%n", spec.qualifiedName());

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : spec.exitCodeOnInvalidInput();
    }
}
