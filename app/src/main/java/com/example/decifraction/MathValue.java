package com.example.decifraction;

import java.math.BigDecimal;

// This type of object can store a value such as π or e, an operation on a value such as √(3) or ln(5), or maybe just a plain old number.
public class MathValue extends MathConcept {
    private BigDecimal value_; // The value of the object (for example 3.14159265358979...).
    private String title_; // The "title" of the object (for example "e" "π" or "8").  This can be the value if the number is a decimal.
    public boolean isApproximateNumber; // This is true when the value stored is something like π, e, or other numbers
    // that should be displayed as symbols rather than as their value.

    // Constructors
    public MathValue(String title, BigDecimal value,  boolean approximateNumber) {
        title_ = title;
        value_ = value;
        isApproximateNumber = approximateNumber;
    }

    public MathValue(String title, String value, boolean approximateNumber) {
        title_ = title;
        value_ = new BigDecimal(value);
        isApproximateNumber = approximateNumber;
    }

    public MathValue(BigDecimal value) {
        title_ = value.toString();
        value_ = value;
        isApproximateNumber = false;
    }

    public MathValue(String value) {
        title_ = value;
        value_ = new BigDecimal(value);
        isApproximateNumber = false;
    }

    // Other methods
    public BigDecimal getValue() {
        return value_;
    }

    public String toString() {
        return title_;
    }

    public void setValue_(BigDecimal value) {
        value_ = value;
    }

    public void setValue_(String value) {
        value_ = new BigDecimal(value);
    }

    public void negate() {
        value_ = (new BigDecimal(0)).subtract(value_);
        updateTitle();
    }

    public void updateTitle() {
        title_ = value_.toString();
    }
}