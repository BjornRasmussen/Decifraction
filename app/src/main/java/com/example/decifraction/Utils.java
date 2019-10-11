package com.example.decifraction;

import java.math.BigDecimal;
import java.math.MathContext;

// Useful BigDecimal tools

public class Utils {

    final static BigDecimal e = new BigDecimal("2.718281828459045235360287471352662497757247093699959574966967627724076630353");


    public static BigDecimal getSqrt(BigDecimal input, BigDecimal wantedPrecision) {
        // TODO remove decimal constraint of decimal 128
        // Returns up to 30 digits of accuracy.
        final BigDecimal TWO = new BigDecimal("2");

        if (input.compareTo(new BigDecimal("0")) == -1) {
            return null;
        }

        BigDecimal currentEstimate = new BigDecimal("0");
        int count = -1;
        while (currentEstimate.multiply(currentEstimate).subtract(input).abs().compareTo(wantedPrecision) == 1) {
            // This method runs if the current precision is not good enough.
            if (currentEstimate.multiply(currentEstimate).compareTo(input) == -1) {
                currentEstimate = currentEstimate.add(input.multiply(TWO.pow(count, MathContext.DECIMAL128)));
            } else if (currentEstimate.multiply(currentEstimate).compareTo(input) == 1) {
                currentEstimate = currentEstimate.subtract(input.multiply(TWO.pow(count, MathContext.DECIMAL128)));
            } else if (currentEstimate.multiply(currentEstimate).compareTo(input) == 0) {
                break;
            }
            count--;
        }
        return currentEstimate;
    }

    public static BigDecimal getSqrt(BigDecimal input, int wantedPrecision) {
        // The wantedPrecision field represents the number of 0s after the dot and before the 1.
        // 3 -> 0.0001
        String precisionString = getPrecisionString(wantedPrecision);
        return getSqrt(input, new BigDecimal(precisionString));
    }

    public static BigDecimal getSqrt(BigDecimal input) {
        return getSqrt(input, 100);
    }

    private static String getPrecisionString(int input) {
        String output = "0.";
        for (int i = 0; i < input; i++) {
            output += "0";
        }
        return output+"1";
    }

    public static BigDecimal round(BigDecimal input) {
        // This method rounds down, and then checks to see if it rounded down by more than 0.5.  If yes, the method adds 1 to the value.
        BigDecimal inputTruncated = new BigDecimal(input.toBigInteger().toString());
        BigDecimal difference = input.subtract(inputTruncated);
        if (difference.compareTo(new BigDecimal("0.5")) == 1) {
            return inputTruncated.add(new BigDecimal("1"));
        } else {
            return inputTruncated;
        }
    }

    public static BigDecimal getLn(BigDecimal input, BigDecimal wantedPrecision) {
//		// TODO remove decimal constraint of decimal 128
//		// Returns up to 30 digits of accuracy.
//		final BigDecimal TWO = new BigDecimal("2");
//
//		BigDecimal currentEstimate = new BigDecimal("0");
//		int count = -1;
//		while (e.pow(currentEstimate).subtract(input).abs().compareTo(wantedPrecision) == 1) {
//			// This method runs if the current precision is not good enough.
//			if (currentEstimate.multiply(currentEstimate).compareTo(input) == -1) {
//				currentEstimate = currentEstimate.add(input.multiply(TWO.pow(count, MathContext.DECIMAL128)));
//			} else if (currentEstimate.multiply(currentEstimate).compareTo(input) == 1) {
//				currentEstimate = currentEstimate.subtract(input.multiply(TWO.pow(count, MathContext.DECIMAL128)));
//			} else if (currentEstimate.multiply(currentEstimate).compareTo(input) == 0) {
//				break;
//			}
//			count--;
//		}
//		return currentEstimate;

        return new BigDecimal(Math.log(input.doubleValue()));// Fixme allow for bigDecimal to the power of bigDecimal above and uncomment.
    }

    public static BigDecimal getLn(BigDecimal input, int wantedPrecision) {
        // The wantedPrecision field represents the number of 0s after the dot and before the 1.
        // 3 -> 0.0001
        String precisionString = getPrecisionString(wantedPrecision);
        return getLn(input, new BigDecimal(precisionString));
    }

    public static BigDecimal getLn(BigDecimal input) {
        return getLn(input, 100);
    }

    public static BigDecimal removeLastNDigits(BigDecimal value, int n) {
        String valueString = value.toString();
        valueString = removeLastNChars(valueString, n);
        return new BigDecimal(valueString);
    }

    public static String removeLastNChars(String input, int n) {
        String output = "";
        for (int i = 0; i < (input.length()-n); i++) {
            output += input.charAt(i);
        }
        return output;
    }

    public static boolean valuesAlmostEqual(BigDecimal a, BigDecimal b, BigDecimal multiplier) {
        int scale = a.scale(); // Both values are assumed to have the same scale.
        if ((scale > b.scale() && b.scale() != 0) || a.scale() == 0) {
            scale = b.scale();
        }
        if (scale == 0) {
            return (a.compareTo(b) == 0);
        }
        if (a.subtract(b).abs().divide(multiplier, MathContext.DECIMAL128).compareTo(new BigDecimal(1).divide(new BigDecimal("10").pow(scale-1))) == -1) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean valuesAlmostEqual(BigDecimal a, BigDecimal b) {
        return valuesAlmostEqual(a, b, new BigDecimal("1"));
    }
}

