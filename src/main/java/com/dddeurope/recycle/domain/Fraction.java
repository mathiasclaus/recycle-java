package com.dddeurope.recycle.domain;

public enum Fraction {
    CONSTRUCTION_WASTE("Construction waste"),
    GREEN_WASTE("Green waste");

    private final String type;

    Fraction(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
