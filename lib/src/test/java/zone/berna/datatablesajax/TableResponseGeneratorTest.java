package zone.berna.datatablesajax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zone.berna.datatablesajax.functionalinterface.PaginatedSearchFunction;
import zone.berna.datatablesajax.functionalinterface.TotalFilteredFunction;
import zone.berna.datatablesajax.request.TableRequest;
import zone.berna.datatablesajax.response.TableResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.*;

class TableResponseGeneratorTest {

    public class TestObject {
        private String name;
        private int index;

        public String getName() {
            return this.name;
        }

        public int getIndex() {
            return this.index;
        }
    }

    private TestObject testObject;

    private LongSupplier totalSupplier;
    private String expectedQuery;
    private String orderingField;
    private String[] expectedFields;
    private TableRequest.Order.Direction expectedDirection;
    private int expectedStart;
    private int expectedCount;
    private TotalFilteredFunction totalFilteredFunction;
    private PaginatedSearchFunction<TestObject> paginatedSearchFunction;
    private TableResponse expectedResponse;
    private TableRequest request;

    @BeforeEach
    void beforeEach() {
        testObject = new TestObject();
        testObject.name = "obj";
        testObject.index = 42;

        totalSupplier = () -> 10;
        expectedQuery = "query";
        orderingField = "name";
        expectedFields = new String[]{"name", "index"};
        expectedDirection = TableRequest.Order.Direction.DESCENDING;
        expectedStart = 42;
        expectedCount = 21;

        totalFilteredFunction = (s) -> Objects.equals(s, expectedQuery) ? 5 : 0;

        //Verifies that every parameter was correctly passed
        paginatedSearchFunction = ((query, orderByField, orderDirection, start, count) -> (
                Objects.equals(query, expectedQuery) &&
                        Objects.equals(orderByField, orderingField) &&
                        Objects.equals(orderDirection, expectedDirection) &&
                        start == expectedStart &&
                        count == expectedCount
        ) ? Collections.singletonList(testObject) : Collections.emptyList());

        List<String> expectedResponseData = new ArrayList<>();
        expectedResponseData.add(testObject.getName());
        expectedResponseData.add(String.valueOf(testObject.getIndex()));

        expectedResponse = TableResponse.builder()
                .draw(1)
                .recordsTotal(10)
                .recordsFiltered(5)
                .data(Collections.singletonList(expectedResponseData))
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
                request, totalSupplier, totalFilteredFunction, paginatedSearchFunction, expectedFields
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
        orderingField = "random";
        expectedFields = new String[]{orderingField};
        assertThrows(IllegalArgumentException.class, this::generate);
    }

}