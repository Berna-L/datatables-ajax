package zone.berna.datatablesajax;

import lombok.NonNull;
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
 * Class that generates a {@link TableResponse} given a {@link TableRequest},
 * functions to retrieve counters and elements, and an array of the fields used as columns.
 *
 */
public class TableResponseGenerator<E> {

    private TableRequest tableRequest;
    private LongSupplier totalCountSupplier;
    private TotalFilteredFunction totalFilteredFunction;
    private PaginatedSearchFunction<E> paginatedSearchFunction;
    private String[] fields;

    private TableResponseGenerator() {
    }

    public static <E> TableRequestReceiver builder() {
        return new TableResponseGenerator<E>()::tableRequest;
    }

    /**
     * Sets the {@link TableRequest} received from the view,
     * with the query string (if any), first element of the current page,
     * amount of elements in the current page, columns shown and ordering
     * (which column and order).
     *
     * @param tableRequest the {@link TableRequest} instance
     * @return the receiver for the supplier of the total number of records,
     * the next step for building a {@link TableResponse}
     */
    public TotalCountSupplierReceiver tableRequest(@NonNull TableRequest tableRequest) {
        this.tableRequest = tableRequest;
        return this::totalCountSupplier;
    }

    /**
     * Sets the function that retrieves the total number of records
     * for the table.
     *
     * @param totalCountSupplier the supplier
     * @return the receiver for the function that retrieves
     * the total number of entries matching the given query string,
     * the next step for building a {@link TableResponse}
     */
    public TotalFilteredFunctionReceiver totalCountSupplier(@NonNull LongSupplier totalCountSupplier) {
        this.totalCountSupplier = totalCountSupplier;
        return this::totalFilteredFunction;
    }

    /**
     * <p>Sets the function that retrieves the total number of entries matching
     * the given query string, which is always trimmed, lowercase and never null.
     *
     * <p>Calling the given function with an empty query string must return the same value as
     * the supplier passed to {@link #totalCountSupplier}.</p>
     *
     * @param totalFilteredFunction the function
     * @return the receiver for the function that returns the request interval of fields,
     * the next step for building a {@link TableResponse}
     */
    public PaginatedSearchFunctionReceiver totalFilteredFunction(@NonNull TotalFilteredFunction totalFilteredFunction) {
        this.totalFilteredFunction = totalFilteredFunction;
        return this::paginatedSearchFunction;
    }

    /**
     * Sets the Function that returns the requested interval of (filtered) elements,
     * sorted by the given field, in ascending/descending order.
     * The query string is always trimmed and in lowercase.
     * An empty query string should return the interval of all elements.
     *
     * @param paginatedSearchFunction the function
     * @return the receiver for the field name array,
     * the next step for building a {@link TableResponse}
     */
    public FieldsReceiver paginatedSearchFunction(@NonNull PaginatedSearchFunction<E> paginatedSearchFunction) {
        this.paginatedSearchFunction = paginatedSearchFunction;
        return this::fields;
    }

    /**
     * Sets the array of field names of the element used in the table,
     * in the same order of the columns
     *
     * @param fields the field names array
     * @return the {@link TableResponseGenerator},
     * the final step for building a {@link TableResponse}
     */
    public TableResponseGenerator fields(@NonNull String... fields) {
        if (fields.length < 1) {
            throw new IllegalArgumentException("Field array must not be empty!");
        }
        this.fields = fields;
        return this;
    }

    /**
     * Builds the {@link TableResponse} given the parameters
     * set during the builder steps.
     *
     * @return the {@link TableResponse}
     */
    public TableResponse build() {
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
     * @return the TableResponse instance, which should be returned to the view
     * as a JSON response object
     * @deprecated use {@link #builder()} and its steps instead
     */
    @Deprecated
    public TableResponse generateResponse(
            TableRequest tableRequest,
            LongSupplier totalCountSupplier,
            TotalFilteredFunction totalFilteredFunction,
            PaginatedSearchFunction<E> paginatedSearchFunction,
            String... fields
    ) {
        return TableResponseGenerator.builder()
                .tableRequest(tableRequest)
                .totalCountSupplier(totalCountSupplier)
                .totalFilteredFunction(totalFilteredFunction)
                .paginatedSearchFunction(paginatedSearchFunction)
                .fields(fields)
                .build();
    }

    /**
     * Builds a row of the table.
     *
     * @param entry  the element to be processed into a row
     * @param fields the array of fields to be used as columns
     * @return a List of the column values
     * @throws IllegalArgumentException if there's no getter fot the field
     */
    private List<String> buildRow(E entry, String... fields) {
        return Stream.of(fields)
                .map(f -> getStringForField(entry, f).orElseThrow(IllegalArgumentException::new))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the string representation of a field, using its {@code toString()} method.
     *
     * @param entry the element that contains the field
     * @param field the name of the field
     * @return the string representation of the field
     * @throws IllegalArgumentException if there's no getter fot the field
     */
    private Optional<String> getStringForField(E entry, String field) {
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

    /**
     * Interface that defines a function that, given a {@link TableRequest},
     * returns a {@link TotalCountSupplierReceiver}.
     * <p>
     * To be used as a step for the generation of a {@link TableResponse}.
     */
    @FunctionalInterface
    public interface TableRequestReceiver {
        TotalCountSupplierReceiver tableRequest(TableRequest tableRequest);
    }

    /**
     * Interface that defines a function that, given a {@link LongSupplier},
     * returns a {@link TotalFilteredFunctionReceiver}.
     * <p>
     * To be used as a step for the generation of a {@link TableResponse}.
     */
    @FunctionalInterface
    public interface TotalCountSupplierReceiver {
        TotalFilteredFunctionReceiver totalCountSupplier(LongSupplier totalCountSupplier);
    }

    /**
     * Interface that defines a function that, given a {@link TotalFilteredFunction},
     * returns a {@link PaginatedSearchFunctionReceiver}.
     * <p>
     * To be used as a step for the generation of a {@link TableResponse}.
     */
    @FunctionalInterface
    public interface TotalFilteredFunctionReceiver {
        PaginatedSearchFunctionReceiver totalFilteredFunction(TotalFilteredFunction totalFilteredFunction);
    }

    /**
     * Interface that defines a function that, given a {@link PaginatedSearchFunction},
     * returns a {@link FieldsReceiver}.
     * <p>
     * To be used as a step for the generation of a {@link TableResponse}.
     */
    @FunctionalInterface
    public interface PaginatedSearchFunctionReceiver {
        FieldsReceiver paginatedSearchFunction(PaginatedSearchFunction paginatedSearchFunction);
    }

    /**
     * Interface that defines a function that, given a list of {@link String Strings} representing field names,
     * returns a {@link TableResponseGenerator}.
     * <p>
     * To be used as a step for the generation of a {@link TableResponse}.
     */
    @FunctionalInterface
    public interface FieldsReceiver {
        TableResponseGenerator fields(String... fields);
    }
}
