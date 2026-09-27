package com.parkinglot.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.parkinglot.app.application.di.ApplicationModule;
import com.parkinglot.app.cli.*;
import com.parkinglot.app.di.GuiceFactory;
import com.parkinglot.app.di.ParkingModule;
import com.parkinglot.app.exception.PrintExceptionMessageHandler;
import com.parkinglot.app.exception.ShortErrorMessageHandler;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "parking",
        mixinStandardHelpOptions = true,
        version = "parking 1.0",
        description = "Parking Lot Application",
        subcommands = {
                CreateParkingLotCommand.class,
                ReserveSpotCommand.class,
                FindVehicleCommand.class,
                ParkVehicleCommand.class,
                DeleteParkingLotCommand.class
        }
)
public class ParkingCliApp {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new ParkingModule(),
                new ApplicationModule()
        );

        int exitCode = new CommandLine(
                injector.getInstance(ParkingCliApp.class),
                new GuiceFactory(injector)
        )
                .setParameterExceptionHandler(new ShortErrorMessageHandler())
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute(args);

        System.exit(exitCode);
    }
}
