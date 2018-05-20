package zone.berna.datatablesajax;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Helper class that simplifies syntax to assert that a function
 * throws a {@link NullPointerException}.
 */
public class AssertNPE {

    public static void of(Executable e) {
        assertThrows(NullPointerException.class, e);
    }

}
