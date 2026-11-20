package edu.itc.canteen.core;

import java.util.regex.Pattern;

/**
 * Card numbers have the form CP-DDDDDDDD-C: the prefix "CP-", eight digits, a hyphen and a
 * Luhn check digit computed over the eight digits.
 */
public final class CardNumber {
    public static final String PREFIX = "CP-";
    private static final Pattern FORMAT = Pattern.compile("CP-(\\d{8})-(\\d)");

    private CardNumber() {}

    /** Luhn check digit over a string of digits (the digit that makes the Luhn sum a multiple of 10). */
    public static int checkDigit(String digits) {
        if (digits == null || !digits.matches("\\d+")) throw new IllegalArgumentException("digits only");
        int sum = 0;
        boolean doubleIt = true;                       // the rightmost payload digit is doubled
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return (10 - (sum % 10)) % 10;
    }

    /** Builds the card number of a serial between 1 and 99 999 999. */
    public static String of(long serial) {
        if (serial < 1 || serial > 99_999_999L) throw new IllegalArgumentException("serial out of range");
        String digits = String.format("%08d", serial);
        return PREFIX + digits + "-" + checkDigit(digits);
    }

    /** True when the string has the exact format and the check digit matches. No trimming, upper case only. */
    public static boolean isValid(String number) {
        if (number == null) return false;
        var m = FORMAT.matcher(number);
        if (!m.matches()) return false;
        return checkDigit(m.group(1)) == m.group(2).charAt(0) - '0';
    }
}
