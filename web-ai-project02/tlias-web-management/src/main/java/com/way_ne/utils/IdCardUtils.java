package com.way_ne.utils;

import java.util.regex.Pattern;

public final class IdCardUtils {
    private static final Pattern ID_CARD_PATTERN =
            Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$");
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    private IdCardUtils() {
    }

    public static boolean isValid(String idCard) {
        if (idCard == null) {
            return false;
        }
        String value = idCard.trim();
        return ID_CARD_PATTERN.matcher(value).matches() && checkCodeValid(value);
    }

    private static boolean checkCodeValid(String idCard) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * WEIGHTS[i];
        }
        char expected = CHECK_CODES[sum % 11];
        char actual = Character.toUpperCase(idCard.charAt(17));
        return expected == actual;
    }
}
