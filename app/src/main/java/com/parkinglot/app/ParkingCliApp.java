package com.parkinglot.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.parkinglot.app.application.di.ApplicationModule;
import com.parkinglot.app.cli.CreateParkingLotCommand;
import com.parkinglot.app.di.GuiceFactory;
import com.parkinglot.app.di.ParkingModule;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "parking",
        mixinStandardHelpOptions = true,
        version = "parking 1.0",
        description = "Parking Lot Application",
        subcommands = {
                CreateParkingLotCommand.class
        }
)
public class ParkingCliApp {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new ParkingModule(),
                new ApplicationModule()
        );

        injector.getInstance(ParkingCliApp.class);
        int exitCode = new CommandLine(
                injector.getInstance(ParkingCliApp.class),
                new GuiceFactory(injector)
        )
                .execute(args);

        System.exit(exitCode);
    }
}
