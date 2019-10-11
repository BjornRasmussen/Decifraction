package com.example.decifraction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

// This represents the Sqrt Operator.
public class SquareRootOperator extends Operator {
    String title = "√";
    MathConcept subject_;
    boolean negative = false;


    // Constructor
    public SquareRootOperator(MathConcept subject) {
        subject_ = subject;
    }

    public BigDecimal getValue() {
        BigDecimal output = Utils.getSqrt(subject_.getValue());
        if (negative) {
            output = output.multiply(new BigDecimal("-1"));
        }
        return output;
    }

    public String toString() {
        String negativeSymbol = "";
        if (negative) {
            negativeSymbol = "-";
        }
        return title + negativeSymbol + "(" + subject_.toString() + ")";
    }

    public void negate() {
        if (!negative) {
            negative = true;
        } else {
            negative = false;
        }
    }
}
