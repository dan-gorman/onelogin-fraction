package dgorman.onelogin;

import java.text.ParseException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Solves arithmetic expressions involving integers and fractions (proper, improper and mixed).
 */
public class Solver {
    // maps operator tokens to math operations
    private static final Map<Lexer.Operator, Consumer<Stack<Fraction>>> opMap;

    static {
        opMap = new HashMap<>();
        opMap.put(Lexer.Operator.ADD, Solver::addFunc);
        opMap.put(Lexer.Operator.MULTIPLY, Solver::multiplyFunc);
        opMap.put(Lexer.Operator.SUBTRACT, Solver::subtractFunc);
        opMap.put(Lexer.Operator.DIVIDE, Solver::divideFunc);
    }

    private static void addFunc(Stack<Fraction> stk) {
        Fraction arg2 = stk.pop();
        Fraction arg1 = stk.pop();
        Fraction result = Fraction.add(arg1, arg2);
        stk.push(result);
    }

    private static void multiplyFunc(Stack<Fraction> stk) {
        Fraction arg2 = stk.pop();
        Fraction arg1 = stk.pop();
        Fraction result = Fraction.multiply(arg1, arg2);
        stk.push(result);
    }

    private static void subtractFunc(Stack<Fraction> stk) {
        Fraction arg2 = stk.pop();
        Fraction arg1 = stk.pop();
        Fraction result = Fraction.subtract(arg1, arg2);
        stk.push(result);
    }

    private static void divideFunc(Stack<Fraction> stk) {
        Fraction arg2 = stk.pop();
        Fraction arg1 = stk.pop();
        Fraction result = Fraction.divide(arg1, arg2);
        stk.push(result);
    }


    /**
     * Evaluates a string-represented expression
     *
     * @param inputExpression a string containing the expression to evaluate.
     * @return a string representing the result.
     * @throws ParseException thrown if the input string was not a valid expression.
     */
    public static String solve(String inputExpression) throws ParseException {
        Iterable<Lexer.Token> expression = Lexer.processLine(inputExpression);
        Lexer.Value resultToken = solveInfix(expression);
        return resultToken.getValueString();
    }

    /**
     * Evaluates an infix structured expression comprised of lexed tokens.
     *
     * @param infixExpr the sequence of tokens representing the infix expression to evaluate.
     * @return the resulting value (as a Value token)
     * @throws ParseException thrown if the input sequence was not a valid expression.
     */
    private static Lexer.Value solveInfix(Iterable<Lexer.Token> infixExpr) throws ParseException {
        Iterable<Lexer.Token> postfixExpr = convertInfixToPostFix(infixExpr);
        return solvePostFix(postfixExpr);
    }

    /**
     * Evaluates a postfix structured expression comprised of lexed tokens.
     *
     * @param postfixExpr the sequence of tokens representing the postfix expression to evaluate.
     * @return the resulting value (as a Value token)
     * @throws ParseException thrown if the input sequence was not a valid expression.
     */
    private static Lexer.Value solvePostFix(Iterable<Lexer.Token> postfixExpr) {
        Objects.requireNonNull(postfixExpr);
        Stack<Fraction> stk = new Stack<>();
        for (Lexer.Token tok : postfixExpr) {
            if (tok instanceof Lexer.Operator) {
                Consumer<Stack<Fraction>> op = opMap.get(tok);
                op.accept(stk);
            } else {
                stk.push(((Lexer.Value) tok).getValue());
            }
        }
        return new Lexer.Value(stk.pop());
    }

    /**
     * Converts a tokenized infix expression into a postfix one.
     *
     * @param infixExpr the sequence of tokens representing the infix expression to convert.
     * @return the resulting postfix expression sequence
     * @throws ParseException thrown if the input sequence was not a valid expression.
     */
    private static Iterable<Lexer.Token> convertInfixToPostFix(Iterable<Lexer.Token> infixExpr) throws ParseException {
        List<Lexer.Token> out = new LinkedList<>();
        Stack<Lexer.Operator> stk = new Stack<>();
        int operatorCount = 0;          // # of operators must be 1 less than the # of values
        int valueCount = 0;
        Lexer.Token prev = Lexer.Operator.ADD;
        for (Lexer.Token inp : infixExpr) {
            // Mustn't have two of the same token types in a row
            if (prev.getClass().equals(inp.getClass())) {
                throw new ParseException("Line does not parse as a valid expression.", 0);
            }
            if (inp instanceof Lexer.Value) {
                out.add(inp);
                ++valueCount;
            } else if (inp instanceof Lexer.Operator) {
                Lexer.Operator inpOp = (Lexer.Operator) inp;
                while (!stk.isEmpty() && ((stk.peek().comparePrecedence(inpOp) >= 0))) {
                    out.add(stk.pop());
                }
                stk.push(inpOp);
                ++operatorCount;
            }
            prev = inp;
        }
        while (!stk.isEmpty()) {
            out.add(stk.pop());
        }
        if (valueCount != (operatorCount + 1)) {
            throw new ParseException("Line does not parse as a valid expression.", 0);
        }
        return out;
    }
}
