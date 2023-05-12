package com.dddeurope.recycle.domain;

import static com.dddeurope.recycle.domain.Fraction.CONSTRUCTION_WASTE;
import static com.dddeurope.recycle.domain.Fraction.GREEN_WASTE;

import java.util.List;

public class Cities {

    private Cities() {
    }

    public static City find(String cityName) {
        return switch (cityName) {
            case "South Park" -> new City(List.of(new FractionCost(CONSTRUCTION_WASTE, 0.18), new FractionCost(GREEN_WASTE, 0.12)));
            default -> new City(List.of(new FractionCost(CONSTRUCTION_WASTE, 0.15), new FractionCost(GREEN_WASTE, 0.09)));
        };
    }
}
