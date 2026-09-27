package com.parkinglot.app.cli.converter;

import com.parkinglot.app.domain.valueobject.RegistrationNumber;
import picocli.CommandLine;

public class RegistrationNumberConverter implements
        CommandLine.ITypeConverter<RegistrationNumber> {

    @Override
    public RegistrationNumber convert(String value) throws Exception {
        try {
            return new RegistrationNumber(value);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(
                    "Invalid registration: " + value
            );
        }
    }
}
