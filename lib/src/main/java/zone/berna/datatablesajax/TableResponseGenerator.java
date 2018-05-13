package zone.berna.datatablesajax;

import lombok.val;
import zone.berna.datatablesajax.functionalinterface.PaginatedSearchFunction;
import zone.berna.datatablesajax.functionalinterface.TotalFilteredFunction;
import zone.berna.datatablesajax.request.TableRequest;
import zone.berna.datatablesajax.response.TableResponse;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Singleton class that generates a {@link TableResponse} given a {@link TableRequest},
 * functions to retrieve counters and elements, and an array of the fields used as columns.
 */
public class TableResponseGenerator {

    private static TableResponseGenerator instance = new TableResponseGenerator();

    private TableResponseGenerator() {

    }

    /**
     * Retrieves the instance of this class.
     *
     * @return the instance
     */
    public static TableResponseGenerator instance() {
        return instance;
    }

    /**
     * Generates a {@link TableResponse} given a {@link TableRequest},
     * functions to retrieve counters and elements, and an array of the fields used as columns.
     *
     * @param tableRequest            the {@link TableRequest} received from the view
     * @param totalCountSupplier      a Supplier that retrieves the total count of entries
     *                                in the table
     * @param totalFilteredFunction   a Function that returns the count of entries matching the
     *                                given query string.
     *                                <p>The query string is always non-null, trimmed and in lowercase.
     *                                <p>An empty query string should result in
     *                                the same value returned by {@code totalCountSupplier}
     * @param paginatedSearchFunction a Function that returns the requested interval of (filtered) elements,
     *                                sorted by the given field, in ascending/descending order.
     *                                The query string is always trimmed and in lowercase.
     *                                An empty query string should return the interval of all elements
     * @param fields                  an array of field names of the element used in the table,
     *                                in the same order of the columns
     * @param <E>                     the element used in the table. Must implement bean-style getters
     *                                for the fields
     * @return the TableResponse instance, which should be returned to the view
     * as a JSON response object
     */
    public <E> TableResponse generateResponse(
            TableRequest tableRequest,
            LongSupplier totalCountSupplier,
            TotalFilteredFunction totalFilteredFunction,
            PaginatedSearchFunction<E> paginatedSearchFunction,
            String... fields
    ) {
        val query = tableRequest.getSearch().getValue().trim().toLowerCase();
        val orderByField = fields[tableRequest.getOrder().getColumn()];
        return TableResponse.builder()
                .draw(tableRequest.getDraw())
                .recordsTotal(totalCountSupplier.getAsLong())
                .recordsFiltered(totalFilteredFunction.applyAsLong(query))
                .data(paginatedSearchFunction.apply(query, orderByField, tableRequest.getOrder().getDir(), tableRequest.getStart(), tableRequest.getLength())
                        .stream()
                        .map(c -> buildRow(c, fields))
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Builds a row of the table.
     *
     * @param entry  the element to be processed into a row
     * @param fields the array of fields to be used as columns
     * @param <E>    the type of the element.
     * @return a List of the column values
     * @throws IllegalArgumentException if there's no getter fot the field
     */
    private <E> List<String> buildRow(E entry, String... fields) {
        return Stream.of(fields)
                .map(f -> getStringForField(entry, f).orElseThrow(IllegalArgumentException::new))
        .collect(Collectors.toList());
    }

    /**
     * Retrieves the string representation of a field, using its {@code toString()} method.
     *
     * @param entry the element that contains the field
     * @param field the name of the field
     * @param <E> the type of the element
     * @return the string representation of the field
     * @throws IllegalArgumentException if there's no getter fot the field
     */
    private <E> Optional<String> getStringForField(E entry, String field) {
        try {
            return Stream.of(Introspector.getBeanInfo(entry.getClass()).getPropertyDescriptors())
                    .filter(p -> field.equals(p.getName()))
                    .map(p -> {
                        try {
                            return p.getReadMethod().invoke(entry).toString();
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new IllegalStateException(e);
                        }
                    }).findFirst();
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
    }

}
