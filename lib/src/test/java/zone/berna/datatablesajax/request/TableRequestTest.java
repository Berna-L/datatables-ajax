package zone.berna.datatablesajax.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableRequestTest {

    private TableRequest.Column column;

    private TableRequest.Search search;

    private TableRequest.Order order;

    private TableRequest tableRequest;

    //Helper methods
    private void assertNullPointerException(Executable e) {
        assertThrows(NullPointerException.class, e);
    }

    //Search tests
    @Test
    void checkSearchGettersSetters() {
        TableRequest.Search search = new TableRequest.Search();
        String newValue = "newValue";
        assertFalse(search.isRegex());
        assertNotEquals(newValue, search.getValue());
        search.setRegex(true);
        assertTrue(search.isRegex());
        search.setValue(newValue);
        assertEquals(newValue, search.getValue());
    }

    @Test
    void checkSearchNullValue() {
        assertNullPointerException(() -> new TableRequest.Search().setValue(null));
    }

    //Order tests
    @Test
    void checkOrderGettersSetters() {
        TableRequest.Order order = new TableRequest.Order();
        int newColumn = 1;
        TableRequest.Order.Direction newDirection = TableRequest.Order.Direction.DESCENDING;
        assertNotEquals(newColumn, order.getColumn());
        assertNotEquals(newDirection, order.getDir());
        order.setColumn(newColumn);
        assertEquals(newColumn, order.getColumn());
        order.setDir(newDirection.getRequestMapping());
        assertEquals(newDirection, order.getDir());
    }

    @Test
    void checkOrderInvalidDir() {
        assertThrows(IllegalArgumentException.class, () -> new TableRequest.Order().setDir("random"));
    }

    //Column tests
    @Test
    void checkColumnGettersSetters() {
        TableRequest.Column column = new TableRequest.Column();
        int newData = 1;
        String newName = "newName";
        TableRequest.Search newSearch = new TableRequest.Search();
        newSearch.setValue("searchValue");
        assertNotEquals(newData, column.getData());
        assertNotEquals(newName, column.getName());
        assertFalse(column.isOrderable());
        assertFalse(column.isSearchable());
        assertNotEquals(newSearch, column.getSearch());
        column.setData(newData);
        assertEquals(newData, column.getData());
        column.setName(newName);
        assertEquals(newName, column.getName());
        column.setSearch(newSearch);
        assertEquals(newSearch, column.getSearch());
        column.setOrderable(true);
        assertTrue(column.isOrderable());
        column.setSearchable(true);
        assertTrue(column.isSearchable());
    }

    @Test
    void checkColumnNullName() {
        assertNullPointerException(() -> new TableRequest.Column().setName(null));
    }

    @Test
    void checkColumnNullSearch() {
        assertNullPointerException(() -> new TableRequest.Column().setSearch(null));
    }

    //TableRequest methods
    @Test
    void checkGettersSetters() {
        TableRequest tr = new TableRequest();
        int newDraw = 1;
        int newStart = 1;
        int newLength = 1;
        TableRequest.Search newSearch = new TableRequest.Search();
        newSearch.setValue("searchValue");
        List<TableRequest.Column> newColumns = Collections.singletonList(new TableRequest.Column());
        TableRequest.Order newOrder = new TableRequest.Order();
        assertNotEquals(newDraw, tr.getDraw());
        assertNotEquals(newStart, tr.getStart());
        assertNotEquals(newLength, tr.getLength());
        assertNotEquals(newSearch, tr.getSearch());
        assertNotEquals(newColumns, tr.getColumns());
        assertNotEquals(newOrder, tr.getOrder());
        tr.setDraw(newDraw);
        assertEquals(newDraw, tr.getDraw());
        tr.setStart(newStart);
        assertEquals(newStart, tr.getStart());
        tr.setLength(newLength);
        assertEquals(newLength, tr.getLength());
        tr.setSearch(newSearch);
        assertEquals(newSearch, tr.getSearch());
        tr.setColumns(newColumns);
        assertEquals(newColumns, tr.getColumns());
        tr.setOrder(Collections.singletonList(newOrder));
        assertEquals(newOrder, tr.getOrder());
    }

    @Test
    void checkNullSearch() {
        assertNullPointerException(() -> new TableRequest().setSearch(null));
    }

    @Test
    void checkNullColumns() {
        assertNullPointerException(() -> new TableRequest().setColumns(null));
    }

    @Test
    void checkEmptyColumns() {
        assertThrows(IllegalArgumentException.class, () -> new TableRequest().setColumns(Collections.emptyList()));
    }

    @Test
    void checkNullOrder() {
        assertNullPointerException(() -> new TableRequest().setOrder(null));
    }

    @Test
    void checkEmptyOrder() {
        assertThrows(IllegalArgumentException.class, () -> new TableRequest().setOrder(Collections.emptyList()));
    }

}