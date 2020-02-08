package dgorman.onelogin;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;

import static dgorman.onelogin.TestUtils.expectThrow;

public class SolverTest {

    @Test
    public void solve() throws ParseException {
        Assert.assertEquals("11", Solver.solve("11"));
        Assert.assertEquals("11", Solver.solve("9_8/4"));
        Assert.assertEquals("11", Solver.solve("9 + 2"));
        Assert.assertEquals("11", Solver.solve("31/2 - 9/2"));
        Assert.assertEquals("11", Solver.solve("-2_1/2 + 27/2"));
        Assert.assertEquals("2/3", Solver.solve("3/2 * 4/9"));
        Assert.assertEquals("-228181/1960", Solver.solve("-39 - 19_12/49 / 35/42 + -41/40 * 53"));
        Assert.assertEquals("2147483628", Solver.solve("178956969 * 36/3"));
        Assert.assertEquals("+Infinity", Solver.solve("178956969 * 36/3 + 19_1/17"));
        Assert.assertEquals("-Infinity", Solver.solve("178956969 * -36/3 - 21_1/17"));
        Assert.assertEquals("NaN", Solver.solve("11/3 / 0"));

        Assert.assertTrue(expectThrow(ParseException.class, () -> Solver.solve("2/5 / 0_3/5 + 7/2 *")).isPresent());
        Assert.assertTrue(expectThrow(ParseException.class, () -> Solver.solve("+ 2/5 / 0_3/5 + 7/2")).isPresent());
        Assert.assertTrue(expectThrow(ParseException.class, () -> Solver.solve("31/2 + - 9/2")).isPresent());
        Assert.assertTrue(expectThrow(Exception.class, () -> Solver.solve("51 % 13")).isPresent());
        Assert.assertTrue(expectThrow(NullPointerException.class, () -> Solver.solve(null)).isPresent());
    }
}