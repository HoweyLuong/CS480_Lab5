package Lab5;

import java.util.*;
import Lab2.Calculator;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
/**
 * Main class for testing and evaluating arithmetic expressions automatically or manually.
 * It will ask for the question about the automatic and manual expression. For this lab, it generates the random
 * valid and invalid expressions, evaluates them using a reference method and a calcutor to track the results for the correctness
 * It will show the percentage to see the accuracy of the calculator
 * @author howey
 *
 */

public class Main {
    private static final String[] operators = {"+", "-", "*", "/", "^"};
    private static final String[] trigonometric = {"sin", "cos", "tan", "log10", "cot", "ln"};
    private static final String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static Random rand = new Random();
    
    // Statistics tracking
    private static int totalTests = 0;
    private static int correctly = 0;
    private static int incorrectly = 0;
    private static int syntaxIncorrect = 0;
    
    
    /**
     * The main entry point of the program. It provides a menu to run tests manually or automatically or it will exit the program.
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Lab5");
        System.out.println();
        //Use the while loop to choose the option
        while (true) {
            System.out.println("Please choose the option: ");
            System.out.println("1. Run the test automatical");
            System.out.println("2. Run the test manually");
            System.out.println("3. Exit");

            //Scanner the input 
            String input = scanner.nextLine().trim();

            if (input.equals("1")) {
                runAutoma(scanner);
            } else if (input.equals("2")) {
                runTestManual(scanner);
            } else if (input.equals("3")) {
                System.out.println("The program is ended.");
                break;
            } else {
                System.out.println("Please try again.");
            }
        }

        scanner.close();
    }
    /**
     * Prompts the user to enter expression manually and tests with it
     * @param scanner Scanner object for the reading input
     */
    private static void runTestManual(Scanner scanner) {
        System.out.println("Test manually");
        System.out.println("Enter your equation type (exit) if you want to exit:");
        
        while (true) {
            String current = scanner.nextLine().trim();
            
            if (current.equalsIgnoreCase("exit")) {
                break;
            }
            
            testExpress(current);
            System.out.println("\nEnter another equation (or 'exit' to return to menu):");
        }
    }
    /**
     * run automatic tests by generating random expressions and testing them
     * @param sc Scanner object for the reading input
     */
    private static void runAutoma(Scanner sc) {
        System.out.println("Test automatically");
        System.out.println("Enter the number of test cases:");
        
        int tests;
        try {
            tests = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default of 10 tests.");
            tests = 10;
        }
        
        System.out.println("Generating and testing " + tests + " expressions...\n");
        
        // Make the beginning of the test
        totalTests = 0;
        correctly = 0;
        incorrectly = 0;
        syntaxIncorrect = 0;
        
        //Use the for loop to generate the test and also print out the result for that
        for (int i = 0; i < tests; i++) {
            
            boolean generate = rand.nextBoolean();
            String expression = generateExpression(generate);
            
            System.out.println("Test #" + (i + 1) + ":");
            testExpress(expression);
            System.out.println();
        }
        
        // Print overall statistics
        System.out.println("\n----- OVERALL -----");
        System.out.println("Total expressions tested: " + totalTests);
        System.out.println("Correctly expressions: " + correctly);
        System.out.println("Incorrectly expressions: " + incorrectly);
        System.out.println("Syntactically incorrect expressions: " + syntaxIncorrect);
        
        if (totalTests > 0) {
            double accuracy = ((double) correctly / totalTests) * 100;
            System.out.println("Accuracy of your calculator: " + String.format("%.2f", accuracy) + "%");
        }
    }
    /**
     * Tests a expression by comparing the result from the reference evaluator and the calculator
     * @param expression The expression to be tested
     */
    private static void testExpress(String expression) {
        totalTests++;
        
        System.out.println("Expression: " + expression);
        
        
        double Result = 0;
        boolean valid = true;
        try {
            Result = evaluateReference(expression);
            System.out.println("Expected result: " + Result);
        } catch (Exception e) {
            valid = false;
            System.out.println("Error: " + e.getMessage());
        }
        
        // Try to evaluate using your calculator from Lab2
        double yourResult = 0;
        boolean yourValid = true;
        try {
            yourResult = Calculator.evaluateExpression(expression);
            System.out.println("Result: " + yourResult);
        } catch (Exception e) {
            yourValid = false;
            System.out.println("Error: " + e.getMessage());
        }
        
        // Compare the result and also update the syntax of that
        if (!valid && !yourValid) {
            System.out.println("Error");
            syntaxIncorrect++;
            correctly++;
        } else if (valid && yourValid) {
            
            if (Math.abs(Result - yourResult) < 1e-10) {
                System.out.println("MATCH: equation have the same result");
                correctly++;
            } else {
                System.out.println("MISMATCH: Different results");
                incorrectly++;
            }
        } else {
            
            incorrectly++;
        }
    }
    
    /**
     * Evaluates an expression using a reference method
     * @param express The expression to evaluate.
     * @return the result of the evaluation
     */
    private static double evaluateReference(String express) {
        try {
            // Replace curly brackets with parentheses
            express = express.replace("{", "(").replace("}", ")");

            // Use to express the evaluate the expression
            Expression now = new ExpressionBuilder(express).build();
            return now.evaluate();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error: " + e.getMessage());
        }
    }
    
    /**
     * Generates a random expression based on whether it should be correct or incorrect
     * @param correctEx Boolean indicating whether to generate a correct expression
     * @return The generated expression as a string
     */
    private static String generateExpression(boolean correctEx) {
        StringBuilder express = new StringBuilder();
        
        if (correctEx) {
            // TO check the correct expression
            generateCorrectExpression(express, 0, 3 + rand.nextInt(5));
        } else {
            //Have the incorrect expression for that
            int errorType = rand.nextInt(5);
            switch (errorType) {
                case 0: // Bracket not balance
                    express.append(generateNoBrackets());
                    break;
                case 1: // The Operator invalid
                    express.append(generateInvalidOp());
                    break;
                case 2: // Invalid function 
                    express.append(generateInvalidFunctionCall());
                    break;
                case 3: // Division by zero
                    express.append(generateDivisionByZero());
                    break;
                case 4: // Random specialization
                    express.append(generateRandom());
                    break;
            }
        }
        
        return express.toString();
    }
    /**
     * Recursively generates a correct expression with random depth
     * @param sb			The StringBuilder to append the expression
     * @param depth			The current depth of the recursive call.
     * @param maxDepth		The maximum allow depth
     */
    private static void generateCorrectExpression(StringBuilder sb, int depth, int maxDepth) {
        if (depth >= maxDepth) {
            // Add a number
            addRandomNumber(sb);
            return;
        }
        //Have the random for 5 equations
        int choice = rand.nextInt(5);
        
        switch (choice) {
            case 0: // Simple number
                addRandomNumber(sb);
                break;
                
            case 1: // Operation for binary
                generateCorrectExpression(sb, depth + 1, maxDepth);
                sb.append(operators[rand.nextInt(operators.length)]);
                generateCorrectExpression(sb, depth + 1, maxDepth);
                break;
                
            case 2: // Expression Parenthesis
                sb.append("(");
                generateCorrectExpression(sb, depth + 1, maxDepth);
                sb.append(")");
                break;
                
            case 3: // Curly bracketed expression
                sb.append("{");
                generateCorrectExpression(sb, depth + 1, maxDepth);
                sb.append("}");
                break;
                
            case 4: // Function call
                String func = trigonometric[rand.nextInt(trigonometric.length)];
                sb.append(func).append("(");
                generateCorrectExpression(sb, depth + 1, maxDepth);
                sb.append(")");
                break;
        }
    }
    /**
     * Adds a random number to the given StringBuilder
     * 
     * @param sb	The StringBuilder to append the number for that.
     */
    private static void addRandomNumber(StringBuilder sb) {
        // Add a random integer or decimal number
        sb.append(numbers[rand.nextInt(numbers.length)]);
        
        // Add more digits for that to append
        int numDigits = rand.nextInt(5);
        for (int i = 0; i < numDigits; i++) {
            sb.append(numbers[rand.nextInt(numbers.length)]);
        }
        
        // Make a decimal
        if (rand.nextBoolean()) {
            sb.append(".");
            int numDecimalDigits = 1 + rand.nextInt(5);
            for (int i = 0; i < numDecimalDigits; i++) {
                sb.append(numbers[rand.nextInt(numbers.length)]);
            }
        }
    }
    
    /**
     * Generates an incorrect expression with unbalanced brackets
     * @return 	The generated expression as a string
     */
    
    private static String generateNoBrackets() {
        StringBuilder sb = new StringBuilder();
        // have the openning bracket at first
        sb.append("(");
        addRandomNumber(sb);
        sb.append(operators[rand.nextInt(operators.length)]);
        addRandomNumber(sb);
        // add closing brackets
        if (rand.nextBoolean()) {
            sb.append("))");
        }
        return sb.toString();
    }
    
    /**
     * Generates an invalid expression with two consecutive operators.
     * This function creates an expression where the operators appear in the sequence it will have the invalid expression
     * 
     * 
     * @return an invalid expression with consecutive operators.
     */
    
    private static String generateInvalidOp() {
        StringBuilder sb = new StringBuilder();
        // Add two operators in a row
        addRandomNumber(sb);
        sb.append(operators[rand.nextInt(operators.length)]);
        sb.append(operators[rand.nextInt(operators.length)]);
        addRandomNumber(sb);
        return sb.toString();
    }
    /**
     * Generates an invalid function call in an express
     * In this function it helps to create an expression where a function is missing the parentheses or not have the balance parenthese
     * @return a invalid function call expression
     */
    private static String generateInvalidFunctionCall() {
        StringBuilder sb = new StringBuilder();
        // Function without parentheses 
        String func = trigonometric[rand.nextInt(trigonometric.length)];
        sb.append(func);
        if (rand.nextBoolean()) {
            // Missing parentheses entirely
            addRandomNumber(sb);
        } else {
            // Unbalanced parentheses
            sb.append("(");
            addRandomNumber(sb);
            // Sometimes closing parenthesis
            if (rand.nextBoolean()) {
                sb.append(")");
            }
        }
        return sb.toString();
    }
    /**
     * Generates an expression which involves division by zero.
     * This function creates an express which divides by a number by zero
     * this one will have the mathmetically invalid and have the result in an error when evaluated
     * @return a syntactically invalid expression representing division by zero.
     */
    private static String generateDivisionByZero() {
        StringBuilder sb = new StringBuilder();
        addRandomNumber(sb);
        sb.append("/0");
        return sb.toString();
    }
    /**
     * Generates a random invalid expression with the specialization characters.
     * This function which create an expression by mixing valid math charactes with invalid charactes, with the special symbols
     * @return a syntactically invalid expression with the random characters
     */
    private static String generateRandom() {
        StringBuilder sb = new StringBuilder();
        // Just add some random characters that aren't valid in an expression
        String[] temp = {"@", "#", "$", "&", "!", "?", "<", ">", "~", "`"};
        int length = 3 + rand.nextInt(10);
        for (int i = 0; i < length; i++) {
            if (rand.nextBoolean()) {
                sb.append(temp[rand.nextInt(temp.length)]);
            } else {
                if (rand.nextBoolean()) {
                    sb.append(numbers[rand.nextInt(numbers.length)]);
                } else {
                    sb.append(operators[rand.nextInt(operators.length)]);
                }
            }
        }
        return sb.toString();
    }
}