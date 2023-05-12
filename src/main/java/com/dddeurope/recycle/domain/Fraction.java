package com.dddeurope.recycle.domain;

public enum Fraction {
    CONSTRUCTION_WASTE("Construction waste", 0.15),
    GREEN_WASTE("Green waste", 0.09);

    private final String tyoe;
    private final double costPerKg;

    Fraction(String tyoe, double costPerKg) {
        this.tyoe = tyoe;
        this.costPerKg = costPerKg;
    }

    public String getTyoe() {
        return tyoe;
    }

    public double getCostPerKg() {
        return costPerKg;
    }
}
