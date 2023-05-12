package com.dddeurope.recycle.domain;

public class DroppedFraction {
    private final Fraction fraction;
    private final double weight;
    private final City city;

    public DroppedFraction(Fraction fraction, double weight, City city) {
        this.weight = weight;
        this.fraction = fraction;
        this.city = city;
    }

    public double calculateCost() {
        return city.getCost(fraction) * weight;
    }
}
