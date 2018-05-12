package zone.berna.datatablesajax.functionalinterface;

import java.util.function.ToLongFunction;

/**
 * Interface that represents a function that, given a query string,
 * returns the number of matching records.
 *
 * <p>The expected format of the query string (is it trimmed? is it lowercase?)
 * should be defined by the user. If none is defined, expect anything.
 */
public interface TotalFilteredFunction extends ToLongFunction<String> {

}
