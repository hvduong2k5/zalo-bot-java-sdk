package io.github.hvduong2k5.zalobot.util;

/**
 * Simple precondition checks for validating method arguments and state.
 * Inspired by Guava's Preconditions but kept minimal for this SDK.
 */
public final class Preconditions {

    private Preconditions() {
        // Utility class — no instantiation
    }

    /**
     * Ensures that an object reference is not null.
     *
     * @param obj     the object to check
     * @param message the exception message if null
     * @param <T>     the type of the object
     * @return the non-null object
     * @throws NullPointerException if {@code obj} is null
     */
    public static <T> T checkNotNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    /**
     * Ensures that a string is not null and not blank (empty or whitespace-only).
     *
     * @param str     the string to check
     * @param message the exception message if blank
     * @return the non-blank string
     * @throws IllegalArgumentException if {@code str} is null or blank
     */
    public static String checkNotBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    /**
     * Ensures the truth of an expression involving method arguments.
     *
     * @param expression a boolean expression
     * @param message    the exception message if false
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
}

