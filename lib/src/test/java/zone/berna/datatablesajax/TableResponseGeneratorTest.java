package zone.berna.datatablesajax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zone.berna.datatablesajax.functionalinterface.PaginatedSearchFunction;
import zone.berna.datatablesajax.functionalinterface.TotalFilteredFunction;
import zone.berna.datatablesajax.request.TableRequest;
import zone.berna.datatablesajax.response.TableResponse;

import java.util.Collections;
import java.util.Objects;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.*;

class TableResponseGeneratorTest {

    private LongSupplier totalSupplier;
    private String expectedQuery;
    private String expectedField;
    private TableRequest.Order.Direction expectedDirection;
    private int expectedStart;
    private int expectedCount;
    private TotalFilteredFunction totalFilteredFunction;
    private PaginatedSearchFunction<Integer> paginatedSearchFunction;
    private TableResponse expectedResponse;
    private TableRequest request;

    @BeforeEach
    void beforeEach() {
        totalSupplier = () -> 10;
        expectedQuery = "query";
        expectedField = "class";
        expectedDirection = TableRequest.Order.Direction.DESCENDING;
        expectedStart = 42;
        expectedCount = 21;

        totalFilteredFunction = (s) -> Objects.equals(s, expectedQuery) ? 5 : 0;

        //Verifies that every parameter was correctly passed
        paginatedSearchFunction = ((query, orderByField, orderDirection, start, count) -> (
                Objects.equals(query, expectedQuery) &&
                        Objects.equals(orderByField, expectedField) &&
                        Objects.equals(orderDirection, expectedDirection) &&
                        start == expectedStart &&
                        count == expectedCount
        ) ? Collections.singletonList(42) : Collections.emptyList());

        expectedResponse = TableResponse.builder()
                .draw(1)
                .recordsTotal(10)
                .recordsFiltered(5)
                .data(Collections.singletonList(Collections.singletonList(Integer.class.toString())))
                .build();

        request = new TableRequest();
        request.setDraw(1);
        request.setStart(expectedStart);
        request.setLength(expectedCount);

        TableRequest.Search search = new TableRequest.Search();
        search.setValue(expectedQuery);
        request.setSearch(search);

        TableRequest.Order order = new TableRequest.Order();
        order.setDir(TableRequest.Order.Direction.DESCENDING.getRequestMapping());
        order.setColumn(0);
        request.setOrder(Collections.singletonList(order));

        TableRequest.Column column = new TableRequest.Column();
        column.setData(0);
        column.setOrderable(true);
        column.setSearchable(true);
        request.setColumns(Collections.singletonList(column));

    }

    private TableResponse generate() {
        return TableResponseGenerator.instance().generateResponse(
                request, totalSupplier, totalFilteredFunction, paginatedSearchFunction, expectedField
        );

    }

    @Test
    void checkSingletonness() {
        TableResponseGenerator trg = TableResponseGenerator.instance();
        TableResponseGenerator trg2 = TableResponseGenerator.instance();
        assertSame(trg, trg2);
    }

    @Test
    void checkGeneration() {
        TableResponse actualResponse = generate();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void checkNonExistantField() {
        expectedField = "random";
        assertThrows(IllegalArgumentException.class, this::generate);

    }

}