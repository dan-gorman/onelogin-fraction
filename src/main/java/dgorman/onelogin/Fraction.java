package dgorman.onelogin;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Models a rational "Fraction" value.
 */
public class Fraction extends Number implements Comparable<Fraction> {
    /**
     * Constant holding the positive infinity of this type.
     */
    public static final Fraction POSITIVE_INFINITY;
    /**
     * Constant holding the negative infinity of this type.
     */
    public static final Fraction NEGATIVE_INFINITY;
    /**
     * constant holding the greatest (non-infinite) value of this type.
     */
    public static final Fraction MAX_VALUE;
    /**
     * constant holding the least (non-infinite) value of this type.
     */
    public static final Fraction MIN_VALUE;
    /**
     * Constant holding a Not-a-Number (NaN) value of this type.
     */
    public static final Fraction NaN;
    /**
     * Constant holding the value 0 of this type.
     */
    public static final Fraction ZERO;
    /**
     * Constant holding the value 1 of this type.
     */
    public static final Fraction ONE;

    /**
     * The regular expression for parsing fraction strings.
     * matches one of:
     * [sign] integer
     * [sign] numerator / denominator
     * [sign] whole _ numerator / denominator
     */
    private static final String fractionParseRegex =
            "^(?<sign>[+\\-]?)" +
                    "(((?<integer>[0-9]+))" +
                    "|((?<whole>[0-9]+)_)?" +
                    "(?<numerator>[0-9]+)/(?<denominator>[0-9]+))$";

    private static final Pattern fractionParsePattern = Pattern.compile(fractionParseRegex);

    private static final long serialVersionUID = 2020020220L;
    private static final String NaN_STRING = "NaN";
    private static final String POSITIVE_INFINITY_STRING = "+Infinity";
    private static final String NEGATIVE_INFINITY_STRING = "-Infinity";
    private static final String MIXED_FORMAT = "%d_%d/%d";
    private static final String RATIO_FORMAT = "%d/%d";
    private static final String WHOLE_FORMAT = "%d";

    static {
        ZERO = new Fraction(0);
        ONE = new Fraction(1);
        MAX_VALUE = new Fraction(Integer.MAX_VALUE);
        MIN_VALUE = new Fraction(Integer.MIN_VALUE);
        POSITIVE_INFINITY = new Fraction();
        NEGATIVE_INFINITY = new Fraction();
        NaN = new Fraction();
        POSITIVE_INFINITY.denominator = NEGATIVE_INFINITY.denominator = NaN.denominator = 0;
        POSITIVE_INFINITY.numerator = Integer.MAX_VALUE;
        NEGATIVE_INFINITY.numerator = 1 + Integer.MIN_VALUE;        // +1 so the negation works nicely
        NaN.numerator = 0;
    }

    private int numerator;
    private int denominator;

    private Fraction() {              // needed for deserialization
        numerator = denominator = 0;
    }

    /**
     * Constructs a new Fraction having the specified whole number value.
     *
     * @param intval the whole number value to initialize this Fraction to.
     */
    public Fraction(final int intval) {
        this(intval, 1, false);
    }

    /**
     * Constructs a new Fraction having the specified rational number value.
     * <p>The rational value is reduced to its simplest form.</p>
     *
     * @param num the numerator for the value to initialize this to.
     * @param den the denominator for the value to initialize this to.
     */
    public Fraction(final int num, final int den) {
        this(num, den, true);
    }

    /**
     * Constructs a new Fraction having the specified rational number value.
     *
     * @param num    the numerator for the value to initialize this to.
     * @param den    the denominator for the value to initialize this to.
     * @param reduce whether to reduce this ratio to its simplest form (true) or not.
     */
    protected Fraction(final int num, final int den, final boolean reduce) {
        numerator = num;
        denominator = den;
        if (reduce) {
            normalize();
        }
    }

    /**
     * Constructs a new Fraction having the specified mixed fraction number value.
     * <p>The value is reduced to its simplest rational form.</p>
     *
     * @param whole the whole number part of the value to initialize this to.
     * @param num   the numerator for the value to initialize this to.
     * @param den   the denominator for the value to initialize this to.
     */
    public static Fraction fromMixedFraction(final int whole, final int num, final int den) {
        final int sign = (whole < 0) || ((whole == 0) && (num < 0)) ? -1 : 1;
        long d = Math.abs((long) den);
        long n = Math.abs((long) num);
        long w = Math.abs((long) whole);
        n += (w * d);
        n *= sign;
        return createFromLongs(n, d, true);
    }

    /**
     * Returns the constant object equivalent to the specified value if it exists;
     * otherwise it returns the original object.
     *
     * @param val the value to convert.
     * @return the equivalent constant value (or the original val)
     */
    private static Fraction mapToConstant(final Fraction val) {
        if (val == null) {
            return null;
        }
        if (val.isNaN()) {
            return NaN;
        }
        if (val.isInfinityPos()) {
            return POSITIVE_INFINITY;
        }
        if (val.isInfinityNeg()) {
            return NEGATIVE_INFINITY;
        }
        if (val.denominator == 1) {
            if (val.numerator == 1) {
                return ONE;
            }
            if (val.numerator == 0) {
                return ZERO;
            }
        }
        return val;
    }

    private static boolean isNullOrEmpty(final String str) {
        return (str == null) || str.isEmpty();
    }

    private static Integer safeParseInt(final String str) {
        if (isNullOrEmpty(str)) {
            return null;
        }
        return Integer.parseInt(str);
    }

    /**
     * Parses a string representation of a fraction into a fraction.
     * Supports integers, and regular, improper and mixed fractions:
     * [sign] integer
     * [sign] numerator / denominator
     * [sign] whole _ numerator / denominator
     *
     * @param fracStr the string to parse
     * @return the fraction
     * @throws NumberFormatException thrown if fracStr is not parsable as a fraction.
     */
    public static Fraction parse(String fracStr) throws NumberFormatException {
        Objects.requireNonNull(fracStr);
        fracStr = fracStr.trim();
        if (fracStr.isEmpty()) {
            throw new NumberFormatException("String not parsable as Fraction.");
        }

        Matcher match = fractionParsePattern.matcher(fracStr);
        if (!match.matches()) {
            throw new NumberFormatException("String not parsable as Fraction.");
        }
        int sign = 1;
        if (!isNullOrEmpty(match.group("sign"))) {
            if (match.group("sign").equals("-")) {
                sign = -1;
            }
        }
        Integer intVal = safeParseInt(match.group("integer"));
        Integer wholeVal = safeParseInt(match.group("whole"));
        Integer numVal = safeParseInt(match.group("numerator"));
        Integer denVal = safeParseInt(match.group("denominator"));

        if (intVal != null) {
            return new Fraction(sign * intVal);
        }
        if (wholeVal != null) {
            if (wholeVal == 0) {
                numVal *= sign;
            } else {
                wholeVal *= sign;
            }
            return Fraction.fromMixedFraction(wholeVal, numVal, denVal);
        }
        return new Fraction(sign * numVal, denVal);
    }

    /**
     * Creates an estimated Fraction from the specified double value.
     *
     * @param val   the double value to convert
     * @param delta the greatest (exclusive) allowable difference between val and the returned fraction.
     * @return the estimated fraction
     */
    public static Fraction fromDouble(final double val, final double delta) {
        throw new UnsupportedOperationException("Not yet implemented");        // @TODO
    }

    private static long gcf(long a, long b) {
        if (a < 0L) {
            a = -a;
        }
        if (b < 0L) {
            b = -b;
        }
        if (a < b) {
            return gcf(b, a);
        }
        if (a == 0L) {
            return 1L;
        }
        while (b != 0L) {
            final long c = a % b;
            a = b;
            b = c;
        }
        return a;
    }

    private static Fraction createFromLongs(long num, long den, final boolean reduce) {
        if (reduce) {
            final long red = gcf(num, den);
            den /= red;
            num /= red;
        }
        if (den > Integer.MAX_VALUE) {
            return ZERO;
        }
        if (den == 0) {
            return NaN;
        }
        if (num > Integer.MAX_VALUE) {
            return POSITIVE_INFINITY;
        }
        if (num < Integer.MIN_VALUE) {
            return NEGATIVE_INFINITY;
        }
        return new Fraction((int) num, (int) den, false);
    }

    /**
     * Returns a Fraction whose value is the negated value of the specified Fraction.
     *
     * @param val the value to get the negated value of.
     * @return the negated value.
     */
    public static Fraction negate(final Fraction val) {
        if (val == null) {
            return null;
        }
        return new Fraction(-1 * val.numerator, val.denominator, false);
    }

    /**
     * Returns a Fraction whose value is the reciprocal value of the specified Fraction.
     *
     * @param val the value to get the reciprocal of.
     * @return the reciprocal value. (or NaN if == 0).
     */
    public static Fraction reciprocal(final Fraction val) {
        if (val == null) {
            return null;
        }
        if (val.isInfinity()) {
            return Fraction.ZERO;
        }
        if ((val.denominator == 0) || (val.numerator == 0)) {
            return Fraction.NaN;
        }
        return new Fraction(val.denominator, val.numerator, true);
    }

    /**
     * Returns a Fraction whose value is the sum of two specified Fractions.
     *
     * @param a One of the values to add together.
     * @param b The other one of the values to add together.
     * @return the sum. (or NaN or Infinity).
     */
    public static Fraction add(final Fraction a, final Fraction b) {
        if ((a == null) || (b == null)) {
            return null;
        }

        // special value addition rules:
        if (a.isNaN() || b.isNaN()) {
            return NaN;
        }
        if (a.isInfinity() && b.isInfinity() && (a.isInfinityPos() != b.isInfinityPos())) {
            return NaN;
        }
        if (a.isInfinity()) {
            return mapToConstant(a);
        }
        if (b.isInfinity()) {
            return mapToConstant(b);
        }

        final long red = gcf(a.denominator, b.denominator);
        final long aScalar = (long) (a.denominator) / red;
        final long bScalar = (long) (b.denominator) / red;
        final long commonDenom = aScalar * bScalar * red;
        final long resultNumer = (aScalar * b.numerator) + (bScalar * a.numerator);
        return createFromLongs(resultNumer, commonDenom, true);
    }

    /**
     * Returns a Fraction whose value is the product of two specified Fractions.
     *
     * @param a One of the values to multiply together.
     * @param b The other one of the values to multiply together.
     * @return the sum. (or NaN or Infinity).
     */
    public static Fraction multiply(final Fraction a, final Fraction b) {
        if ((a == null) || (b == null)) {
            return null;
        }
        if (a.isNaN() || b.isNaN()) {
            return NaN;
        }
        if (a.isInfinity() && b.isInfinity()) {
            return NaN;
        }
        if (a.isInfinity() || b.isInfinity()) {
            int tgtSign = a.getSign() * b.getSign();
            return (tgtSign < 0)
                    ? Fraction.NEGATIVE_INFINITY
                    : ((tgtSign > 0)
                    ? Fraction.POSITIVE_INFINITY
                    : Fraction.ZERO);
        }
        long aN = a.numerator;
        long aD = a.denominator;
        long bN = b.numerator;
        long bD = b.denominator;
        long g = gcf(aN, bD);
        aN /= g;
        bD /= g;
        g = gcf(aD, bN);
        aD /= g;
        bN /= g;

        aN *= bN;
        aD *= bD;
        return createFromLongs(aN, aD, false);
    }

    /**
     * Returns a Fraction whose value is the difference of two specified Fractions.
     *
     * @param a The number that b is subtracted from (minuend).
     * @param b The number that is subtracted from a (subtrahend).
     * @return the difference. (or NaN or Infinity).
     */
    public static Fraction subtract(final Fraction a, final Fraction b) {
        return add(a, negate(b));
    }

    /**
     * Returns a Fraction whose value is the quotient of two specified Fractions.
     *
     * @param a The number that b is divided into (dividend).
     * @param b The number that is divided into a (divisor).
     * @return the difference. (or NaN or Infinity).
     */
    public static Fraction divide(final Fraction a, final Fraction b) {
        return multiply(a, reciprocal(b));
    }

    /**
     * Returns the numerator of this fraction.
     */
    public int getNumerator() {
        return numerator;
    }

    /**
     * Returns the denominator of this fraction.
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Returns the numerator (when viewed as a mixed fraction) of this fraction.
     */
    public int getMixedNumerator() {
        if (denominator == 0) {
            throw new ArithmeticException("NaN or Infinities cannot represented as a mixed fraction.");
        }
        int num = numerator < 0 ? -numerator : numerator;
        num %= denominator;
        return num;
    }

    /**
     * Returns the whole number value (when viewed as a mixed fraction) of this fraction.
     */
    public int getMixedWhole() {
        if (denominator == 0) {
            throw new ArithmeticException("NaN or Infinities cannot represented as a mixed fraction.");
        }
        return numerator / denominator;
    }

    private int getSign() {
        return (numerator < 0) ? -1 : ((numerator > 0) ? +1 : 0);
    }

    /**
     * Applies rules to simplify a Fraction into its simplest and most uniform form.
     */
    private void normalize() {
        // converts zero-denominator value into an infinity or NaN.
        if (denominator == 0) {
            numerator = (numerator < 0) ?
                    (NEGATIVE_INFINITY.numerator) :
                    ((numerator > 0) ?
                            POSITIVE_INFINITY.numerator :
                            0);
        } else {
            // keep the sign in the numerator
            if (denominator < 0) {
                denominator = -denominator;
                numerator = -numerator;
            }
            // reduce the fraction
            final int g = (int) gcf(numerator, denominator);
            numerator /= g;
            denominator /= g;
        }
    }

    /**
     * Returns true if this is Not-a-Number (NaN).
     */
    boolean isNaN() {
        return (denominator == 0) && !isInfinity();
    }

    /**
     * Returns true if this is an infinity.
     */
    boolean isInfinity() {
        return (denominator == 0) && (isInfinityPos() || isInfinityNeg());
    }

    /**
     * Returns true if this is positive infinity.
     */
    boolean isInfinityPos() {
        return (denominator == 0) && (numerator == POSITIVE_INFINITY.numerator);
    }

    /**
     * Returns true if this is negative infinity.
     */
    boolean isInfinityNeg() {
        return (denominator == 0) && (numerator == NEGATIVE_INFINITY.numerator);
    }

    /**
     * Returns a hash value for this Fraction.
     */
    @Override
    public int hashCode() {
        return (((Integer) numerator).hashCode() * 23) + ((Integer) denominator).hashCode();
    }

    /**
     * Tests this Fraction against an object for equivalency.
     *
     * @param o the object to test this against.
     * @return true if equivalent
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof Fraction) && this.equals((Fraction) o);
    }

    /**
     * Returns a string representation of this Fraction.
     */
    @Override
    public String toString() {
        return this.toString(RenderMode.RATIO);     // render as non-mixed by default
    }

    /**
     * Returns a string representation of this Fraction, explicitly specifying how
     * improper "top-heavy" fractions are dealt with.
     *
     * @param mode how certain fractions are represented - mixed or a ratio.
     */
    public String toString(RenderMode mode) {
        if (this.isInfinityPos()) {
            return POSITIVE_INFINITY_STRING;
        }
        if (this.isInfinityNeg()) {
            return NEGATIVE_INFINITY_STRING;
        }
        if (this.isNaN()) {
            return NaN_STRING;
        }

        if (denominator == 1) {     // integer only?
            return String.format(WHOLE_FORMAT, numerator);
        }
        if (Math.abs(numerator) > Math.abs(denominator)) {      // improper?
            if (mode == RenderMode.MIXED) {
                return String.format(MIXED_FORMAT, this.getMixedWhole(), this.getMixedNumerator(), this.getDenominator());
            }
        }
        return String.format(RATIO_FORMAT, numerator, denominator);
    }

    /**
     * Tests this Fraction against another for equivalency.
     *
     * @param that the Fraction to test this against.
     * @return true if equivalent
     */
    public boolean equals(Fraction that) {
        return (that != null) &&
                (this.numerator == that.numerator) &&
                (this.denominator == that.denominator);
    }

    // ////////////////////////////////////////////////////
    // Comparable implementation

    /**
     * Compares this Fraction against another numerically.
     *
     * @param that the Fraction to compare this against.
     * @return 0 if numerically equal; less than 0 if this Fraction is numerically less than the other; and greater than 0 if this Fraction is numerically greater than the other.
     */
    @Override
    public int compareTo(Fraction that) {
        if (that == null) {
            return -1;                      // interpret nulls as greater than anything else
        }
        if (this.isNaN() || that.isNaN()) {
            throw new ArithmeticException("Comparisons involving NaN(s) are undefined!");
        }
        if (this.isInfinity()) {
            if (that.isInfinity() && (this.isInfinityPos() == this.isInfinityPos())) {
                throw new ArithmeticException("Comparisons between same signed infinities are undefined!");
            }
            return this.isInfinityPos() ? +1 : -1;
        }
        long leftVal = this.numerator * that.denominator;
        long rightVal = this.denominator * that.numerator;
        return Long.compare(leftVal, rightVal);
    }

    /**
     * Returns the value of the specified number as a byte, which may involve rounding or truncation.
     */
    @Override
    public byte byteValue() {
        return (byte) (Math.min(Byte.MAX_VALUE, Math.max(Byte.MIN_VALUE, this.longValue())));
    }

    // ////////////////////////////////////////////////////
    // Number implementation

    /**
     * Returns the value of the specified number as a double, which may involve rounding.
     */
    @Override
    public double doubleValue() {
        if (this.isNaN()) {
            return Double.NaN;
        }
        if (this.isInfinityPos()) {
            return Double.POSITIVE_INFINITY;
        }
        if (this.isInfinityNeg()) {
            return Double.NEGATIVE_INFINITY;
        }
        return Math.min(Double.MAX_VALUE, Math.max(Double.MIN_VALUE, (double) numerator / (double) denominator));
    }

    /**
     * Returns the value of the specified number as a float, which may involve rounding.
     */
    @Override
    public float floatValue() {
        if (this.isNaN()) {
            return Float.NaN;
        }
        if (this.isInfinityPos()) {
            return Float.POSITIVE_INFINITY;
        }
        if (this.isInfinityNeg()) {
            return Float.NEGATIVE_INFINITY;
        }
        return (float) Math.min(Float.MAX_VALUE, Math.max(Float.MIN_VALUE, doubleValue()));
    }

    /**
     * Returns the value of the specified number as an int, which may involve rounding or truncation.
     */
    @Override
    public int intValue() {
        return (int) (Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, this.longValue())));
    }

    /**
     * Returns the value of the specified number as a long, which may involve rounding or truncation.
     */
    @Override
    public long longValue() {
        if (isNaN()) {
            throw new ArithmeticException("NaN has no integer representation.");
        }
        if (isInfinity()) {
            throw new ArithmeticException("Infinity has no integer representation.");
        }
        return (long) numerator / (long) denominator;
    }

    /**
     * Returns the value of the specified number as a short, which may involve rounding or truncation.
     */
    @Override
    public short shortValue() {
        return (short) (Math.min(Short.MAX_VALUE, Math.max(Short.MIN_VALUE, this.longValue())));
    }

    // ////////////////////////////////////////////////////
    // Serialization support
    private void writeObject(final java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    private void readObjectNoData() throws ObjectStreamException {
        numerator = denominator = 0;    // NaN if munged up.
    }

    /**
     * Indicates how "Top Heavy" fractions are rendered into strings.
     */
    public enum RenderMode {
        /**
         * render improper fraction to string as a ratio.
         */
        RATIO,
        /**
         * render improper fraction to string as a mixed fraction (whole + ratio).
         */
        MIXED
    }
}
