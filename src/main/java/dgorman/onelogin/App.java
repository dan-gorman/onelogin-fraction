package dgorman.onelogin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Fraction math solver application
 * <p>
 * Basic version - requires fractions and operators to be whitespace delimited.
 *
 * @author dgorman
 * @TODO readme
 */
public class App {
    public static void main(String[] args) {
        String commandLineInput = null;
        if (args.length > 0) {

            // Look for a help option on the command line
            for (String anArg : args) {
                if (anArg.equalsIgnoreCase("-h") || anArg.equalsIgnoreCase("--help") || anArg.equals("-?")) {
                    showHelp();
                    return;
                }
            }

            // otherwise, treat the command line as an expression to solve.
            commandLineInput = String.join(" ", args);
        }

        if ((commandLineInput != null) && !commandLineInput.isEmpty()) {
            try {
                System.out.printf("? %s\n", commandLineInput);
                String result = Solver.solve(commandLineInput);
                System.out.printf("= %s\n", result);
            } catch (Exception ex) {
                System.err.printf("! Error evaluating expression: %s", ex.getMessage());
            }
            return;
        }
        try {
            try (BufferedReader inp = new BufferedReader(new InputStreamReader(System.in))) {
                String inputExpression = null;
                do {
                    System.out.print("? ");
                    System.out.flush();
                    inputExpression = inp.readLine();
                    if (inputExpression != null) {
                        inputExpression = inputExpression.trim();
                        if (!inputExpression.isEmpty()) {
                            try {
                                String result = Solver.solve(inputExpression);
                                System.out.printf("= %s\n", result);
                            } catch (Exception ex) {
                                System.err.printf("! Error evaluating expression: %s", ex.getMessage());
                            }
                        }
                    }
                } while ((inputExpression != null) && !inputExpression.isEmpty());
            }
        } catch (IOException ex) {
            System.err.printf("Error reading from input: %s\n", ex.getMessage());
        }
    }

    private static void showHelp() {
        System.out.println("Usage: ");
        System.out.println("    FractionTest");
        System.out.println("            Application will prompt for expressions to solve until an empty line or an EOF is entered.");
        System.out.println("        or");
        System.out.println("    FractionTest <expression>");
        System.out.println("            Application solve the provided expression and exit.");
        System.out.println("        or");
        System.out.println("    FractionTest --help");
        System.out.println("            Displays this help.");
    }
}
