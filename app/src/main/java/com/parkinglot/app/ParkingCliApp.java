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
import picocli.CommandLine.Model.CommandSpec;

@Command(
        name = "parking",
        description = "Manage a parking lot, vehicles and reservations.",
        mixinStandardHelpOptions = true,
        version = "parking 1.0",
        synopsisSubcommandLabel = "COMMAND",
        commandListHeading = "%ncommands:%n",
        optionListHeading = "%noptions:%n",
        footer = "%nRun 'parking help COMMAND' for command details.",
        exitCodeListHeading = "%nExit codes:%n",
        exitCodeList = {
                "0:Successful operation or help",
                "1:Internal or Storage failure",
                "2:Invalid command or input",
                "3:Operation rejected by a business rule"
        },
        subcommands = {
                CreateParkingLotCommand.class,
                ReserveSpotCommand.class,
                FindVehicleCommand.class,
                ParkVehicleCommand.class,
                UnparkVehicleCommand.class,
                DeleteParkingLotCommand.class,
                ParkingStatusCommand.class
        }
)
public class ParkingCliApp implements Runnable {

    @CommandLine.Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }

    public static void main(String[] args) {
        // Guice reads this flag when its internals initialize in the native executable.
        if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
            System.setProperty("guice_bytecode_gen_option", "DISABLED");
        }
        Injector injector = Guice.createInjector(
                new ParkingModule(),
                new ApplicationModule()
        );

        CommandLine cli = new CommandLine(
                injector.getInstance(ParkingCliApp.class),
                new GuiceFactory(injector)
        )
                .setCaseInsensitiveEnumValuesAllowed(true)
                .setParameterExceptionHandler(new ShortErrorMessageHandler())
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler());

        System.exit(cli.execute(args));
    }
}
