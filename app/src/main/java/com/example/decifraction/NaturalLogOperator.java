package com.example.decifraction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

// This represents the Sqrt Operator.
public class NaturalLogOperator extends Operator {
    String title = "ln";
    MathConcept subject_;
    boolean negative = false;


    // Constructor
    public NaturalLogOperator(MathConcept subject) {
        subject_ = subject;
    }

    public BigDecimal getValue() {
        BigDecimal output = Utils.getLn(subject_.getValue());
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
}
