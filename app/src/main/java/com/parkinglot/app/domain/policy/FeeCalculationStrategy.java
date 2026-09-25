package com.parkinglot.app.domain.policy;

import com.google.inject.Inject;
import com.parkinglot.app.domain.model.Ticket;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

public class FeeCalculationStrategy {

    private static final BigDecimal CAR_PER_HOUR = BigDecimal.valueOf(20);
    private static final BigDecimal BIKE_PER_HOUR = BigDecimal.valueOf(10);

    @Inject
    public FeeCalculationStrategy() {}

    public Money calculate(Ticket ticket, Instant exitTime) {
        // Calculate Duration
        Duration duration = ticket.duration();

        BigDecimal rate = getRateForVehicleType(ticket.vehicleType());

        BigDecimal fee = rate.multiply(
                BigDecimal.valueOf(duration.toMinutes()).divide(
                        BigDecimal.valueOf(Duration.ofHours(1).toMinutes()),
                        RoundingMode.HALF_DOWN
                )
        );

        return new Money(fee);
    }

    private BigDecimal getRateForVehicleType(VehicleType vehicleType) {
        return switch (vehicleType) {
            case CAR -> CAR_PER_HOUR;
            case BIKE -> BIKE_PER_HOUR;
        };
    }

}
