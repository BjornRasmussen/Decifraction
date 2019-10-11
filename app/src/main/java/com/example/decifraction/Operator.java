package com.example.decifraction;

// Type object type is meant to surround a MathObject with an operation.
// For example, a MathObject of value √π would store a MathObject π associated with an Operator √

public abstract class Operator extends MathConcept {
    public String title;
    boolean negative;
    public abstract String toString();

    public void negate() {
        if (!negative) {
            negative = true;
        } else {
            negative = false;
        }
    }
}
