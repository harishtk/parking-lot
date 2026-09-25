package com.parkinglot.app.domain.model;

public enum VehicleType { CAR(2), BIKE(1);

    private final int unitSize;

    VehicleType(int uniSize) {
        this.unitSize = uniSize;
    }

    public int getUnitSize() { return unitSize; }
}
