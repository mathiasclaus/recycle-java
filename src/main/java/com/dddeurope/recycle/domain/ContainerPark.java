package com.dddeurope.recycle.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ContainerPark {

    private final List<FractionCost> fractionCosts;

    public ContainerPark(List<FractionCost> fractionCosts) {
        this.fractionCosts = fractionCosts;
    }

    public double calculateTotalCost(List<DroppedFraction> droppedFractions) {
        var totalCost = droppedFractions.stream()
            .mapToDouble(this::calculateCost)
            .sum();

        return roundTwoDecimals(totalCost);
    }

    private double calculateCost(DroppedFraction droppedFraction) {
        return getCost(droppedFraction.fraction()) * droppedFraction.weight();
    }

    private double getCost(Fraction fraction) {
        return fractionCosts.stream()
            .filter(fractionCost -> fractionCost.fraction().equals(fraction))
            .findFirst()
            .map(FractionCost::cost)
            .orElse(0.0);
    }

    private static double roundTwoDecimals(double totalCost) {
        return BigDecimal.valueOf(totalCost).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
