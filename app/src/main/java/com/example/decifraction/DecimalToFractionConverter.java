package com.example.decifraction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class DecimalToFractionConverter {

    public DecimalToFractionConverter() {

    }

    public Fraction calcFraction(String input) {
        return calcFraction(new BigDecimal(input));
    }

    public Fraction calcFraction(BigDecimal input) {
        return getFractionSimpleMethod(input);
    }

    public Fraction calcFraction(BigDecimal input, Fraction multiplier) {
        Fraction result = getFractionSimpleMethod(input, multiplier);
        result.multiply(multiplier);
        return result;
    }

    private Fraction getFractionSimpleMethod(BigDecimal input) {
        return getFractionSimpleMethod(input, new Fraction(new BigDecimal("1"), new BigDecimal("1")));
    }

    private Fraction getFractionSimpleMethod(BigDecimal input, Fraction whatTheFractionIsMultipliedBy) {

        // If the input fraction multiplier is null, make it equal to 1.
        if (whatTheFractionIsMultipliedBy == null) {
            whatTheFractionIsMultipliedBy = new Fraction(); // Create new Fraction with value 1/1.
        }


        double whatFracMultBy = whatTheFractionIsMultipliedBy.getValue().doubleValue();
        double decimal = input.doubleValue()/ whatFracMultBy;
        int numberOfCharsAfterPoint = input.precision() - input.stripTrailingZeros().precision();
        int precision = numberOfCharsAfterPoint + 5;
        final long highestNumerator = 100000000;
        final long MAX_CALCULATION_TIME = 500;

        long currentBestNumerator =  1;  // This is for when no fractions have been found, so the best match has to be tested.
        double currentSmallestError = 1;

        long time = System.currentTimeMillis();
        long startTime = time;

        for (int i = 1; i < highestNumerator; i++) {
            // Numerator of fraction we are looking for / decimal input = integer denominator of fraction we are looking for.
            if (firstTermIsTheNumeratorOfAFractionEqualToSecondTerm(i, decimal/whatFracMultBy, precision)){
                BigDecimal numerator = new BigDecimal(i + "");
                BigDecimal denominator = Utils.round(numerator.divide(input, MathContext.DECIMAL128));

                // Now, test to make sure that the fraction found actually matches the digits of the input.
                BigDecimal inputValue = input;
                BigDecimal valueOfFraction = numerator.divide(denominator, inputValue.scale(), RoundingMode.DOWN);
                valueOfFraction = valueOfFraction.divide(whatTheFractionIsMultipliedBy.getValue(), MathContext.DECIMAL128);

                if (Utils.valuesAlmostEqual(inputValue, valueOfFraction, whatTheFractionIsMultipliedBy.getValue())) {
                    // This runs if the fraction's digits match all of the input's digits.
                    Fraction returning = new Fraction(numerator, denominator);
                    returning.simplify();
                    returning.setError(inputValue.subtract(valueOfFraction).abs());
                    return returning;
                } else if (currentSmallestError > Math.abs(inputValue.subtract(valueOfFraction).doubleValue())){
                    currentBestNumerator = i;
                    currentSmallestError = Math.abs(inputValue.subtract(valueOfFraction).doubleValue());
                }
            }

            // Only let the loop run for 1 second.
            if (i % 10000 == 0) {
                if (System.currentTimeMillis() - startTime > MAX_CALCULATION_TIME) {
                    break;
                }
            }
        }
        if (currentBestNumerator != 1) {
            BigDecimal numerator = new BigDecimal(currentBestNumerator);
            BigDecimal denominator = Utils.round(numerator.divide(input, MathContext.DECIMAL128));
            Fraction returning = new Fraction(numerator, denominator);
            returning.simplify();
            return returning;
        }
        return null;
    }

    private boolean firstTermIsTheNumeratorOfAFractionEqualToSecondTerm(double numerator, double decimal, int precision) {
        // The precision represents how close the fraction must be to the decimal input.
        // 3 -> 1/10^3 -> accuracy of 0.001.
        if (Math.round(numerator/decimal) == 0) {
            // This is to fix the error of Decifraction returning 1/0 for large inputs.
            return false;
        }
        return (Math.abs((numerator/decimal)-Math.round(numerator/decimal)) < Math.pow(10, precision*-1));
    }
}
