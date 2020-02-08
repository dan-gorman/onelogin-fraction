package dgorman.onelogin;

import org.junit.Assert;
import org.junit.Test;

import static dgorman.onelogin.TestUtils.expectThrow;

public class FractionTest {
    @Test
    public void fromMixedFraction() {
        Assert.assertEquals(new Fraction(3, 2), Fraction.fromMixedFraction(1, 2, 4));
        Assert.assertEquals(Fraction.ONE, Fraction.fromMixedFraction(1, 0, 12));
        Assert.assertEquals(Fraction.ZERO, Fraction.fromMixedFraction(0, 0, 1));
        Assert.assertEquals(new Fraction(-85, 7), Fraction.fromMixedFraction(-12, 1, 7));
        Assert.assertEquals(new Fraction(-85, 7), Fraction.fromMixedFraction(-12, -1, 7));
        Assert.assertEquals(new Fraction(-85, 7), Fraction.fromMixedFraction(-12, 1, -7));
        Assert.assertEquals(new Fraction(-85, 7), Fraction.fromMixedFraction(-12, -1, -7));
        Assert.assertEquals(Fraction.POSITIVE_INFINITY, Fraction.fromMixedFraction(Integer.MAX_VALUE, 1, 2));
        Assert.assertEquals(Fraction.NEGATIVE_INFINITY, Fraction.fromMixedFraction(Integer.MIN_VALUE, 2, 3));
        Assert.assertEquals(Fraction.NaN, Fraction.fromMixedFraction(1, 2, 0));
    }

    @Test
    public void getNumerator() {
        Assert.assertEquals(2, (new Fraction(2)).getNumerator());
        Assert.assertEquals(0, Fraction.NaN.getNumerator());
    }

    @Test
    public void getDenominator() {
        Assert.assertEquals(4, (new Fraction(1, 4)).getDenominator());
        Assert.assertEquals(0, Fraction.NaN.getDenominator());
    }

    @Test
    public void getMixedNumerator() {
        Assert.assertEquals(3, (new Fraction(-13, 5)).getMixedNumerator());
    }

    @Test
    public void getMixedWhole() {
        Assert.assertEquals(-2, (new Fraction(-13, 5)).getMixedWhole());
    }

    @Test
    public void negate() {
        Assert.assertEquals(new Fraction(13, 5), Fraction.negate(new Fraction(-13, 5)));
        Assert.assertEquals(Fraction.ZERO, Fraction.negate(Fraction.ZERO));
        Assert.assertEquals(Fraction.NaN, Fraction.negate(Fraction.NaN));
    }

    @Test
    public void reciprocal() {
        Assert.assertEquals(new Fraction(-5, 13), Fraction.reciprocal(new Fraction(-13, 5)));
        Assert.assertEquals(Fraction.NaN, Fraction.reciprocal(new Fraction(0, 5)));
        Assert.assertEquals(Fraction.NaN, Fraction.reciprocal(Fraction.NaN));
    }

    @Test
    public void add() {
        Assert.assertEquals(new Fraction(2), Fraction.add(new Fraction(5, 4), new Fraction(3, 4)));
        Assert.assertEquals(new Fraction(31, 12), Fraction.add(new Fraction(5, 4), new Fraction(4, 3)));
        Assert.assertEquals(Fraction.NaN, Fraction.add(new Fraction(5, 4), Fraction.NaN));
        Assert.assertEquals(Fraction.NaN, Fraction.add(Fraction.POSITIVE_INFINITY, Fraction.NEGATIVE_INFINITY));
        Assert.assertEquals(Fraction.POSITIVE_INFINITY, Fraction.add(Fraction.POSITIVE_INFINITY, Fraction.POSITIVE_INFINITY));
    }

    @Test
    public void multiply() {
        Assert.assertEquals(new Fraction(6, 7), Fraction.multiply(new Fraction(3, 5), new Fraction(10, 7)));
        Assert.assertEquals(Fraction.NaN, Fraction.multiply(new Fraction(5, 4), Fraction.NaN));
        Assert.assertEquals(Fraction.NEGATIVE_INFINITY, Fraction.multiply(new Fraction(-5, 4), Fraction.POSITIVE_INFINITY));
    }

    @Test
    public void subtract() {
        Assert.assertEquals(new Fraction(1, 4), Fraction.subtract(new Fraction(5, 8), new Fraction(3, 8)));
        Assert.assertEquals(new Fraction(-49, 24), Fraction.subtract(new Fraction(5, 8), new Fraction(8, 3)));
        Assert.assertEquals(Fraction.NaN, Fraction.subtract(new Fraction(5, 8), Fraction.NaN));
    }

    @Test
    public void divide() {
        Assert.assertEquals(new Fraction(6, 7), Fraction.divide(new Fraction(3, 5), new Fraction(7, 10)));
        Assert.assertEquals(Fraction.NaN, Fraction.divide(new Fraction(3, 5), Fraction.ZERO));
        Assert.assertEquals(Fraction.NaN, Fraction.divide(new Fraction(5, 4), Fraction.NaN));
        Assert.assertEquals(Fraction.ZERO, Fraction.divide(new Fraction(5, 4), Fraction.POSITIVE_INFINITY));
    }

    @Test
    public void isNaN() {
        Assert.assertTrue(Fraction.NaN.isNaN());
        Assert.assertFalse(Fraction.POSITIVE_INFINITY.isNaN());
        Assert.assertFalse(Fraction.NEGATIVE_INFINITY.isNaN());
        Assert.assertFalse(Fraction.ONE.isNaN());
    }

    @Test
    public void isInfinity() {
        Assert.assertTrue(Fraction.POSITIVE_INFINITY.isInfinity());
        Assert.assertTrue(Fraction.NEGATIVE_INFINITY.isInfinity());
        Assert.assertFalse(Fraction.NaN.isInfinity());
        Assert.assertFalse(Fraction.ZERO.isInfinity());
    }

    @Test
    public void isInfinityPos() {
        Assert.assertTrue(Fraction.POSITIVE_INFINITY.isInfinityPos());
        Assert.assertFalse(Fraction.NEGATIVE_INFINITY.isInfinityPos());
        Assert.assertFalse(Fraction.NaN.isInfinityPos());
        Assert.assertFalse(Fraction.ZERO.isInfinityPos());
    }

    @Test
    public void isInfinityNeg() {
        Assert.assertFalse(Fraction.POSITIVE_INFINITY.isInfinityNeg());
        Assert.assertTrue(Fraction.NEGATIVE_INFINITY.isInfinityNeg());
        Assert.assertFalse(Fraction.NaN.isInfinityNeg());
        Assert.assertFalse(Fraction.ZERO.isInfinityNeg());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("3/11", (new Fraction(3, 11)).toString(Fraction.RenderMode.RATIO));
        Assert.assertEquals("27/5", (new Fraction(27, 5)).toString(Fraction.RenderMode.RATIO));
        Assert.assertEquals("-43/11", (new Fraction(-43, 11)).toString(Fraction.RenderMode.RATIO));
        Assert.assertEquals("1", Fraction.ONE.toString(Fraction.RenderMode.RATIO));
        Assert.assertEquals("0", Fraction.ZERO.toString(Fraction.RenderMode.RATIO));
    }

    @Test
    public void testToStringMixed() {
        Assert.assertEquals("3/11", (new Fraction(3, 11)).toString(Fraction.RenderMode.MIXED));
        Assert.assertEquals("5_2/5", (new Fraction(27, 5)).toString(Fraction.RenderMode.MIXED));
        Assert.assertEquals("-3_10/11", (new Fraction(-43, 11)).toString(Fraction.RenderMode.MIXED));
        Assert.assertEquals("1", Fraction.ONE.toString(Fraction.RenderMode.MIXED));
        Assert.assertEquals("0", Fraction.ZERO.toString(Fraction.RenderMode.MIXED));
    }

    @Test
    public void testEquals() {
        Assert.assertTrue((new Fraction(3, 5)).equals(new Fraction(6, 10)));
        Assert.assertEquals(new Fraction(5, 3), ((Object) new Fraction(50, 30)));
        Assert.assertFalse((new Fraction(5, 3)).equals(new Fraction(6, 10)));
        Assert.assertFalse((new Fraction(6, 10)).equals(null));
        Assert.assertNotEquals("not a fraction", new Fraction(3, 5));
    }

    @Test
    public void testParse() {
        Assert.assertEquals(Fraction.ZERO, Fraction.parse("0"));
        Assert.assertEquals(Fraction.ONE, Fraction.parse("1"));
        Assert.assertEquals(new Fraction(3, 11), Fraction.parse("3/11"));
        Assert.assertEquals(new Fraction(27, 5), Fraction.parse("27/5"));
        Assert.assertEquals(new Fraction(27, 5), Fraction.parse("5_2/5"));
        Assert.assertEquals(new Fraction(-43, 11), Fraction.parse("-3_10/11"));
        Assert.assertEquals(new Fraction(-43, 11), Fraction.parse("-43/11"));
    }

    @Test
    public void testParseBad() {
        Assert.assertTrue(expectThrow(NullPointerException.class, () -> Fraction.parse(null)).isPresent());
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Fraction.parse("")).isPresent());
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Fraction.parse("123_")).isPresent());
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Fraction.parse("123/234_345")).isPresent());
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Fraction.parse("123/234/345")).isPresent());
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Fraction.parse("123_456")).isPresent());
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Fraction.parse("_234")).isPresent());
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Fraction.parse("-/234")).isPresent());
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Fraction.parse("abc/def")).isPresent());
    }


    @Test
    public void compareTo() {
        Assert.assertTrue((Fraction.ZERO).compareTo(new Fraction(2, 3)) < 0);
        Assert.assertTrue((new Fraction(3, 4)).compareTo(new Fraction(2, 3)) > 0);
        Assert.assertEquals(0, (new Fraction(2, 3)).compareTo(new Fraction(8, 12)));
        Assert.assertTrue(Fraction.ZERO.compareTo(null) < 0);
    }

    @Test
    public void byteValue() {
        Assert.assertEquals((byte) 0, Fraction.ZERO.byteValue());
        Assert.assertEquals(Byte.MAX_VALUE, new Fraction(401, 2).byteValue());
        Assert.assertEquals(Byte.MIN_VALUE, new Fraction(-401, 2).byteValue());
    }

    @Test
    public void doubleValue() {
        Assert.assertEquals(1.5, new Fraction(6, 4).doubleValue(), (0.5 / 4.0));
        Assert.assertEquals(Double.NaN, Fraction.NaN.doubleValue(), Double.MIN_VALUE);
    }

    @Test
    public void floatValue() {
        Assert.assertEquals((float) 2.5, new Fraction(10, 4).floatValue(), (0.5 / 8.0));
        Assert.assertEquals(Float.NaN, Fraction.NaN.floatValue(), Float.MIN_VALUE);
    }

    @Test
    public void intValue() {
        Assert.assertEquals(0, Fraction.ZERO.intValue());
        Assert.assertEquals(12, new Fraction(12, 1).intValue());
        Assert.assertEquals(-2, new Fraction(-16, 7).intValue());
    }

    @Test
    public void longValue() {
        Assert.assertEquals(0, Fraction.ZERO.longValue());
        Assert.assertEquals(1, new Fraction(5, 3).longValue());
        Assert.assertEquals(-2, new Fraction(-5, 2).longValue());
    }

    @Test
    public void shortValue() {
        Assert.assertEquals((short) 0, Fraction.ZERO.shortValue());
        Assert.assertEquals(Short.MAX_VALUE, new Fraction(70000, 1).shortValue());
        Assert.assertEquals(Short.MIN_VALUE, new Fraction(-70000, 1).shortValue());
    }

    @Test
    public void testNormalize() {
        Assert.assertTrue(new Fraction(2100, -42).equals(new Fraction(-50)));
        Assert.assertTrue(new Fraction(-77, -21).equals(new Fraction(11, 3)));
        Assert.assertTrue(new Fraction(0, 0).isNaN());
        Assert.assertTrue(new Fraction(12, 0).isInfinityPos());
        Assert.assertTrue(new Fraction(-23, 0).isInfinityNeg());
    }

    @Test
    public void testExceptions() {
        Assert.assertTrue(
                expectThrow(ArithmeticException.class,
                        () -> Fraction.NaN.getMixedNumerator())
                        .isPresent());
        Assert.assertTrue(
                expectThrow(ArithmeticException.class,
                        () -> Fraction.POSITIVE_INFINITY.getMixedWhole())
                        .isPresent());
        Assert.assertTrue(
                expectThrow(ArithmeticException.class,
                        () -> Fraction.ONE.compareTo(Fraction.NaN))
                        .isPresent());
        Assert.assertTrue(
                expectThrow(ArithmeticException.class,
                        () -> Fraction.POSITIVE_INFINITY.compareTo(Fraction.POSITIVE_INFINITY))
                        .isPresent());
        Assert.assertTrue(
                expectThrow(ArithmeticException.class,
                        () -> Fraction.POSITIVE_INFINITY.longValue())
                        .isPresent());
        Assert.assertTrue(
                expectThrow(ArithmeticException.class,
                        () -> Fraction.NaN.intValue())
                        .isPresent());
    }
}