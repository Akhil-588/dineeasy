package com.dineeasy.util;

import java.util.Scanner;

/**
 * Console input helpers that loop until valid input is provided.
 */
public class InputValidator {

    private static final Scanner scanner = new Scanner(System.in);

    private InputValidator() {}

    /**
     * Reads an integer from stdin, re-prompting on invalid input.
     *
     * @param prompt message shown before reading
     * @return valid integer entered by the user
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a non-negative double from stdin, re-prompting on invalid input.
     *
     * @param prompt message shown before reading
     * @return valid double ≥ 0 entered by the user
     */
    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value < 0) {
                    System.out.println("  [!] Value cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Please enter a numeric value.");
            }
        }
    }

    /**
     * Reads a non-blank string from stdin, re-prompting if empty.
     *
     * @param prompt message shown before reading
     * @return trimmed, non-empty string
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("  [!] Input cannot be empty. Please try again.");
        }
    }

    /** Reads any string from stdin (may be empty). */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** Exposes the shared Scanner for callers that need raw line reads. */
    public static Scanner getScanner() {
        return scanner;
    }
}
