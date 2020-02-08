package dgorman.onelogin;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Utility method collection for Tests
 */
public final class TestUtils {
    private TestUtils() {
        // Please don't instantiate!
    }

    /**
     * Executes a function from which an exception of a specified type (and optionally matching message) is thrown.
     *
     * @param throwType Class object of the Throwable to expected to be thrown.
     * @param testFunc  the function to execute (void return, no parameters).
     * @param <T>       the Throwable type expected.
     * @return the caught object if thrown; empty otherwise.
     * <p>
     * Note: this could be replaced by assertThrows in JUnit 5.
     */
    static public <T extends Throwable> Optional<T> expectThrow(Class<T> throwType, Callable testFunc) {
        try {
            testFunc.call();
        } catch (Throwable ex) {
            if (throwType.isAssignableFrom(ex.getClass())) {
                return Optional.of((T) ex);
            }
            // not a match -- fall through to empty/fail result.
        }
        return Optional.empty();
    }

}
