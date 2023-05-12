package com.dddeurope.recycle.domain;

public class DroppedFraction {
    private final Fraction fraction;
    private final double weight;

    public DroppedFraction(Fraction fraction, double weight) {
        this.weight = weight;
        this.fraction = fraction;
    }

    public double calculateCost() {
        return fraction.getCostPerKg() * weight;
    }
}
