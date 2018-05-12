package zone.berna.datatablesajax.functionalinterface;

import zone.berna.datatablesajax.request.TableRequest;

import java.util.List;

/**
 * Interface that represents a function that retrieves a range of records given
 * filtering, sorting and ordering parameters.
 *
 * @param <E> the type of the to-be-retrieved record
 */
@FunctionalInterface
public interface PaginatedSearchFunction<E> {

    /**
     * Retrieves a list of a defined range of all records that match the given query,
     * sorted by the given field and ordered in the given direction.
     *
     * @param query          the query string
     * @param orderByField   field used by ordering
     * @param orderDirection ordering direction (ascending or descending)
     * @param start          index of first record
     * @param count          quantity of records to be retrieved
     * @return the list of retrieved records
     */
    List<E> apply(String query, String orderByField, TableRequest.Order.Direction orderDirection, int start, int count);
}
