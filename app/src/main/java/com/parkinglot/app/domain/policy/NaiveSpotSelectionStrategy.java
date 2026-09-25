package com.parkinglot.app.domain.policy;

import com.parkinglot.app.domain.model.ParkingSpot;
import com.parkinglot.app.domain.model.Reservation;
import com.parkinglot.app.domain.model.VehicleType;
import com.parkinglot.app.domain.valueobject.RegistrationNumber;

import java.util.List;
import java.util.Optional;

public class NaiveSpotSelectionStrategy implements SpotSelectionStrategy {


    @Override
    public Optional<ParkingSpot> selectSpot(VehicleType vehicleType,
                                            RegistrationNumber registrationNumber,
                                            List<ParkingSpot> candidateSpots,
                                            List<Reservation> reservations) {

        if (candidateSpots.isEmpty()) {return Optional.empty();}

        for (ParkingSpot spot : candidateSpots) {
            if (spot.canAccommodate(vehicleType.getUnitSize())) {
                return Optional.of(spot);
            }
        }

        return Optional.empty();
    }
}
