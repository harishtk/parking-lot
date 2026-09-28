package com.parkinglot.app;

import org.junit.jupiter.api.Test;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.util.Modules;
import com.parkinglot.app.application.di.ApplicationModule;
import com.parkinglot.app.di.GuiceFactory;
import com.parkinglot.app.di.ParkingModule;
import com.parkinglot.app.domain.model.ParkingLot;
import com.parkinglot.app.domain.repository.ParkingLotRepository;
import picocli.CommandLine;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test void rootCommandPrintsUsage() {
        StringWriter output = new StringWriter();
        // Help must wire every command without touching the user's saved parking lot.
        var injector = Guice.createInjector(Modules.override(new ParkingModule()).with(new AbstractModule() {
            @Override protected void configure() {
                bind(ParkingLotRepository.class).toInstance(new ParkingLotRepository() {
                    public Optional<ParkingLot> load() { throw new AssertionError("Unexpected storage access"); }
                    public boolean save(ParkingLot lot) { throw new AssertionError("Unexpected storage access"); }
                    public boolean delete() { throw new AssertionError("Unexpected storage access"); }
                });
            }
        }), new ApplicationModule());
        CommandLine command = new CommandLine(new ParkingCliApp(), new GuiceFactory(injector));
        command.setOut(new PrintWriter(output));
        assertEquals(0, command.execute());
        assertTrue(output.toString().contains("Usage: parking"));
    }
}
