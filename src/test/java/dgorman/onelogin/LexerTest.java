package dgorman.onelogin;

import org.junit.Assert;
import org.junit.Test;

import static dgorman.onelogin.TestUtils.expectThrow;

public class LexerTest {

    /**
     * Produces a string "serialization" of a token stream.  Mostly intended for testing/debug purposes.
     *
     * @param tokenStream the stream to serialize
     * @return the serialization result
     */
    private static String serializeTokenStream(final Iterable<Lexer.Token> tokenStream) {
        if (tokenStream == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String delim = "";
        for (Lexer.Token tok : tokenStream) {
            result.append(delim);
            if (delim.isEmpty()) {
                delim = " ";
            }
            result.append(tok.toString());
        }
        return result.toString();
    }

    @Test
    public void processLine() {
        Assert.assertEquals("OP[*|2]", serializeTokenStream(Lexer.processLine("*")));

        Assert.assertEquals("VALUE[1]", serializeTokenStream(Lexer.processLine("1")));
        Assert.assertEquals("VALUE[1/3]", serializeTokenStream(Lexer.processLine("+1/3")));
        Assert.assertEquals("VALUE[-7/3]", serializeTokenStream(Lexer.processLine("-2_1/3")));

        Assert.assertEquals("VALUE[-23/5] OP[+|1] VALUE[234]", serializeTokenStream(Lexer.processLine("-4_3/5 + 234")));
        Assert.assertEquals("VALUE[1] OP[/|2] VALUE[23/4] OP[*|2] VALUE[-2]", serializeTokenStream(Lexer.processLine("1 / 23/4 * -2")));
    }

    @Test
    public void processLineTrim() {
        Assert.assertEquals("VALUE[1] OP[/|2] VALUE[23/4] OP[*|2] VALUE[-2]", serializeTokenStream(Lexer.processLine("1 / 23/4 * -2 ")));
        Assert.assertEquals("VALUE[5] OP[/|2] VALUE[32/7] OP[*|2] VALUE[-6]", serializeTokenStream(Lexer.processLine(" 5 / 32/7 * -6")));
    }

    @Test
    public void processLineEmpty() {
        Assert.assertEquals("", serializeTokenStream(Lexer.processLine("   ")));
        Assert.assertEquals("", serializeTokenStream(Lexer.processLine("")));
    }

    @Test
    public void processLineBad() {
        Assert.assertTrue(expectThrow(NumberFormatException.class, () -> Lexer.processLine("-4/3/5 + 2:34")).isPresent());
        Assert.assertTrue(expectThrow(Exception.class, () -> Lexer.processLine("-4_3/5 % 234")).isPresent());
        Assert.assertTrue(expectThrow(NullPointerException.class, () -> Lexer.processLine(null)).isPresent());
    }
}