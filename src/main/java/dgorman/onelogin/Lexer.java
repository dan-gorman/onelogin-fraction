package dgorman.onelogin;

import java.util.*;

/**
 * Parses lines into sequences of Tokens (values, operators...)
 */
public class Lexer {
    /**
     * Parses a string into its equivalent sequences of operator and value tokens.
     *
     * @param line the string to parse
     * @return the token sequence
     */
    public static Iterable<Token> processLine(String line) {
        Objects.requireNonNull(line);
        line = line.trim();
        if (line.isEmpty()) {
            return Collections.emptyList();
        }
        String[] parts = line.split("\\s+");
        List<Token> results = new ArrayList<>(parts.length);
        for (String piece : parts) {
            Optional<Operator> op = Operator.decode(piece);
            if (op.isPresent()) {
                results.add(op.get());
            } else {
                results.add(new Value(piece));
            }
        }
        return results;
    }

    /**
     * Models a general token parsed by this Lexer.
     */
    public static abstract class Token {
        private final String valueString;

        protected Token(final String valueString) {
            this.valueString = valueString;
        }

        public String getValueString() {
            return valueString;
        }

        @Override
        public String toString() {
            return String.format("TOKEN[%s]", valueString);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(valueString);
        }

        @Override
        public boolean equals(Object o) {
            if ((o != null) && (o instanceof Token)) {
                Token that = (Token) o;
                if (valueString == null) {
                    return (that.valueString == null);
                }
                return valueString.equals(that.valueString);
            }
            return false;
        }
    }

    /**
     * Models a value (Fraction) token parsed by this Lexer.
     */
    public static class Value extends Token {
        private final Fraction value;

        public Value(String valueStr) {
            super(valueStr);
            value = Fraction.parse(valueStr);
        }

        public Value(Fraction value) {
            super(value.toString());
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("VALUE[%s]", value.toString());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public boolean equals(Object o) {
            if ((o != null) && (o instanceof Value)) {
                Value that = (Value) o;
                if (value == null) {
                    return that.value == null;
                }
                return value.equals(that.value);
            }
            return false;
        }

        public Fraction getValue() {
            return value;
        }
    }

    /**
     * Models an operator token parsed by this Lexer.
     */
    public static class Operator extends Token {
        public static final Operator ADD;
        public static final Operator MULTIPLY;
        public static final Operator SUBTRACT;
        public static final Operator DIVIDE;

        private static final Map<String, Operator> decodingMap;

        static {
            ADD = new Operator("+", 1);
            MULTIPLY = new Operator("*", 2);
            SUBTRACT = new Operator("-", 1);
            DIVIDE = new Operator("/", 2);
            decodingMap = new HashMap<>();
            decodingMap.put(ADD.getValueString(), ADD);
            decodingMap.put(MULTIPLY.getValueString(), MULTIPLY);
            decodingMap.put(SUBTRACT.getValueString(), SUBTRACT);
            decodingMap.put(DIVIDE.getValueString(), DIVIDE);
        }

        /**
         * Precedence rank of this operator.  Higher integer value means higher precedence.
         */
        private final int rank;

        protected Operator(final String valueString, int rank) {
            super(valueString);
            this.rank = rank;
        }

        /**
         * Identifies a string as an operator and returns its Operator token.
         *
         * @param str the string to interpret.
         * @return the identified Operator as a present Optional (or Empty if unidentifiable)
         */
        public static Optional<Operator> decode(final String str) {
            if (str == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(decodingMap.get(str));
        }

        /**
         * Compares the precedence of this operator against another.
         *
         * @param that the operator to compare this one against.
         * @return 0 if same precedence, < 0 if less than that, > 0 if greater.
         */
        public int comparePrecedence(Operator that) {
            if (that == null) {
                return 1;
            }    // null has no precedence at all
            return Integer.compare(this.rank, that.rank);
        }

        @Override
        public String toString() {
            return String.format("OP[%s|%d]", this.getValueString(), this.rank);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getValueString(), this.rank);
        }

        @Override
        public boolean equals(Object o) {
            return ((o != null) &&
                    (o instanceof Operator) &&
                    super.equals(o) &&
                    (this.rank == ((Operator) o).rank));
        }
    }
}

