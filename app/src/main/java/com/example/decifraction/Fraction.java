package com.example.decifraction;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;

// This object type can store a fraction - a number over another number.
public class Fraction {
    private MathConcept[] numerators_; // For storing all objects in the top of the fraction.
    private MathConcept[] denominators_; // For storing all objects in the bottom of the fraction.

    BigDecimal inaccuracy = new BigDecimal("0");
    BigDecimal one = new BigDecimal("1");
    BigDecimal zero = new BigDecimal("0");

    // Constructor
    public Fraction(MathConcept[] numerators, MathConcept[] denominators) {
        numerators_ = numerators;
        denominators_ = denominators;
    }

    public Fraction(String numerator, String denominator) {
        numerators_ = new MathConcept[1];
        numerators_[0] = new MathValue(numerator);
        denominators_ = new MathConcept[1];
        denominators_[0] = new MathValue(denominator);
    }

    public Fraction(String numerator) {
        numerators_ = new MathConcept[1];
        numerators_[0] = new MathValue(numerator);
        denominators_ = new MathConcept[1];
        denominators_[0] = new MathValue("1");
    }

    public Fraction(MathConcept numerator, MathConcept denominator) {
        numerators_ = new MathConcept[1];
        numerators_[0] = numerator;
        denominators_ = new MathConcept[1];
        denominators_[0] = denominator;
    }

    public Fraction(BigDecimal numerator, BigDecimal denominator) {
        numerators_ = new MathConcept[1];
        numerators_[0] = new MathValue(numerator);
        denominators_ = new MathConcept[1];
        denominators_[0] = new MathValue(denominator);
    }

    public Fraction() {
        numerators_ = new MathConcept[1];
        numerators_[0] = new MathValue("1");
        denominators_ = new MathConcept[1];
        denominators_[0] = new MathValue("1");
    }

    public BigDecimal getValue() {
        BigDecimal numeratorVal = new BigDecimal(1);
        BigDecimal denominatorVal = new BigDecimal(1);
        for (int i = 0; i < numerators_.length; i++) {
            numeratorVal = numeratorVal.multiply(numerators_[i].getValue());
        }
        for (int i = 0; i < denominators_.length; i++) {
            denominatorVal = denominatorVal.multiply(denominators_[i].getValue());
        }
        return numeratorVal.divide(denominatorVal, MathContext.DECIMAL128);  // TODO remove decimal constraint of decimal 128
    }

    public String toString() {
        String output = "";
        if (numerators_.length != 1) output += "(";
        output += numerators_[0];
        for (int i = 1; i < numerators_.length; i++) {
            output += " * " + numerators_[i];
        }
        if (numerators_.length != 1) output += ")";


        output += " / ";


        if (denominators_.length != 1) output += "(";
        output += denominators_[0];
        for (int i = 1; i < denominators_.length; i++) {
            output += " * " + denominators_[i];
        }
        if (denominators_.length != 1) output += ")";


        return output;
    }

    public void simplify() {
        // This method simplifies the fraction to the simplest possible form.

        // First, remove all MathConcepts that have a value of 1
        for (int i = 0; i < numerators_.length; i++) {
            if (numerators_[i].getValue().compareTo(one) == 0) {
                removeNthTerm(numerators_, i);
            }
        }
        for (int i = 0; i < denominators_.length; i++) {
            if (denominators_[i].getValue().compareTo(one) == 0) {
                removeNthTerm(denominators_, i);
            }
        }

        // Next, remove MathConcepts in the top that have the same value as one on the bottom (removing the one of the bottom as well).
        for (int i = 0; i < numerators_.length; i++) {
            for (int j = 0; j < denominators_.length; j++) {
                // This runs for each combination of numerator/denominator.
                if (numerators_[i].getValue().subtract(denominators_[j].getValue()).compareTo(new BigDecimal("0")) == 0) {
                    // This runs if the numerator and denominator being tested have the same value.
                    numerators_ = removeNthTerm(numerators_, i);
                    denominators_ = removeNthTerm(denominators_, j);

                    if (numerators_.length == 0) {
                        numerators_ = new MathValue[1];
                        numerators_[0] = new MathValue("1");
                    }
                    if (denominators_.length == 0) {
                        denominators_ = new MathValue[1];
                        denominators_[0] = new MathValue("1");
                    }

                    break;

                }
            }
        }

        // Next, remove all negatives and place them combined into the first top value.
        int multiplier = 1;  // This holds all negatives / positives.
        for (int i = 0; i < numerators_.length; i++) {
            if (numerators_[i].getValue().compareTo(zero) == -1) {
                // The value is less than zero
                multiplier = 0-multiplier;
                numerators_[i].negate();
            }
        }
        for (int i = 0; i < denominators_.length; i++) {
            if (denominators_[i].getValue().compareTo(zero) == -1) {
                multiplier = 0-multiplier;
                denominators_[i].negate();
            }
        }
        if (multiplier == -1) {
            System.out.println(numerators_[0]);
            numerators_[0].negate();
        }
        System.out.println(numerators_[0]+ " " + denominators_[0]);
    }

    public MathConcept[] removeNthTerm(MathConcept[] input, int n) {
        MathConcept[] outputArray = new MathConcept[input.length-1];

        for (int i = 0; i < input.length; i++) {
            if (i==n) {
                if (i==outputArray.length) {
                    break;
                }
                i++;
            }
            outputArray[i] = input[i];

        }

        return outputArray;
    }

    private MathConcept[] removeNthTerm(int n, MathConcept[] input) {
        return removeNthTerm(input, n);
    }

    public void setError(BigDecimal error) {
        // The "inaccuracy" or "error" is how off this fraction is from the decimal we'er looking for.
        inaccuracy = error;
    }

    public void multiply(Fraction input) {
        int totalNumerators = input.numerators_.length + numerators_.length;
        int totalDenominators = input.denominators_.length + denominators_.length;
        MathConcept[] newNumerators = new MathConcept[totalNumerators];
        MathConcept[] newDenominators = new MathConcept[totalDenominators];

        for (int i = 0; i < numerators_.length; i++) {
            newNumerators[i] = numerators_[i];
        }

        for (int i = numerators_.length; i < totalNumerators; i++) {
            newNumerators[i] = input.numerators_[i];
        }

        for (int i = 0; i < denominators_.length; i++) {
            newDenominators[i] = denominators_[i];
        }

        for (int i = denominators_.length; i < totalDenominators; i++) {
            newDenominators[i] = input.denominators_[i];
        }

        numerators_ = newNumerators;
        denominators_ = newDenominators;

        simplify();
    }
}
