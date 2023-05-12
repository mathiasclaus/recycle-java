package com.dddeurope.recycle.domain;

import java.util.List;

public class City {

    private final List<FractionCost> fractionCosts;

    public City(List<FractionCost> fractionCosts) {
        this.fractionCosts = fractionCosts;
    }

    public double getCost(Fraction fraction) {
        return fractionCosts.stream()
            .filter(fractionCost -> fractionCost.fraction().equals(fraction))
            .findFirst()
            .map(FractionCost::cost)
            .orElse(0.0);
    }
}
