package com.parkinglot.app.domain.valueobject;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSources;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationNumberTest {

    @ParameterizedTest(name = "{0} is expected to output {1}")
    @MethodSource("registrationNumberTestCases")
    void registrationNumber(String vehicleNumber, boolean expected) {
        if (expected) {
            assertDoesNotThrow(() -> new RegistrationNumber(vehicleNumber));
        } else {
            assertThrows(IllegalArgumentException.class, () -> new RegistrationNumber(vehicleNumber));
        }
    }

    private static Stream<Arguments> registrationNumberTestCases() {
        return Stream.of(
                Arguments.of(Named.of("KA 05 AB 1234", "Invalid State Series"), false),
                Arguments.of(Named.of("MH12C3456", "Valid State Series"), true),
                Arguments.of(Named.of("22BH1234AA", "Valid BH Series"), true),
                Arguments.of(Named.of("21 BH 5678 A", "Invalid BH Series"), false),
                Arguments.of(Named.of("XX 99 ZZ 9999X", "Invalid"), false)
        );
    }
}