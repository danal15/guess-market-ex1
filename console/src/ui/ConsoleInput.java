package ui;

import java.util.Scanner;

public class ConsoleInput {

    private final Scanner scanner = new Scanner(System.in);

    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Please choose a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("That is not a number. Please enter a number between " + min + " and " + max + ".");
            }
        }
    }

    public long readPositiveLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                long value = Long.parseLong(line);
                if (value < 1) {
                    System.out.println("Please enter a whole number of 1 or more.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid whole number. Please try again.");
            }
        }
    }

    public String readNonEmptyLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.length() >= 2 && line.startsWith("\"") && line.endsWith("\"")) {
                line = line.substring(1, line.length() - 1).trim();
            }
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("This cannot be empty. Please try again.");
        }
    }
}
