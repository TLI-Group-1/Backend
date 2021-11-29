package tech.autodirect.api.utils;

public class ParseChecker {
    public static boolean isParsableToDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}
