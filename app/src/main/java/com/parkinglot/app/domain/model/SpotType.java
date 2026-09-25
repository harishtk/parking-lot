package com.parkinglot.app.domain.model;

public enum SpotType { CAR(2), BIKE(1);

    private final int unitSize;

    SpotType(int uniSize) {
        this.unitSize = uniSize;
    }

    public int getUnitSize() { return unitSize; }
}
