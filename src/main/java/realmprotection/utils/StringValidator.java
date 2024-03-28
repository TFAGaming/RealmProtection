package realmprotection.utils;

public class StringValidator {
    public static boolean isCleanString(String input) {
        String regex = "^[a-zA-Z0-9]+$";

        return input.matches(regex);
    }
}