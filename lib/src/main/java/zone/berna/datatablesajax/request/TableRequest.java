package zone.berna.datatablesajax.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The object that represents a request for data from DataTables.
 * <p>
 * Usually there's no need to read or write to properties of this class,
 * since all manipulation is done by {@link zone.berna.datatablesajax.TableResponseGenerator TableResponseGenerator}.
 */
@Getter
@Setter
public class TableRequest {

    /**
     * Index of the request. Used by DataTables so a request whose response
     * is received after a later request is ignored.
     */
    private int draw;
    /**
     * Size of the request.
     */
    private int length;
    /**
     * Index of the first element in the table.
     */
    private int start;
    /**
     * List of columns.
     */
    @NonNull
    private List<Column> columns = Collections.emptyList();
    /**
     * Ordering parameters.
     */
    @NonNull
    private Order order = new Order();
    /**
     * Searching parameters.
     */
    @NonNull
    private Search search = new Search();

    public void setColumns(List<Column> columns) {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("Column list must not be empty!");
        }
        this.columns = new ArrayList<>(columns);
    }

    public void setOrder(@NonNull List<Order> order) {
        if (order.isEmpty()) {
            throw new IllegalArgumentException("Order list must not be empty!");
        }
        this.order = order.get(0); //DataTables returns a 1-element Order array, so we retrieve the object itself
    }

    /**
     * Represents a table column.
     */
    @Getter
    @Setter
    public static class Column {
        private int data;
        @NonNull
        private String name = "";
        private boolean orderable;
        @NonNull
        private Search search = new Search();
        private boolean searchable;
    }

    /**
     * Represents a search term.
     */
    @Getter
    @Setter
    public static class Search {
        private boolean regex;
        @NonNull
        private String value = "";
    }

    /**
     * Represents ordering information (column index and ordering direction).
     */
    @Getter
    @Setter
    public static class Order {

        private int column;
        @NonNull
        private Direction dir = Direction.ASCENDING;

        public void setDir(String string) {
            this.dir = Direction.of(string);
        }

        /**
         * Represents the ordering direction of the results.
         */
        @Getter
        public enum Direction {
            ASCENDING("asc"),
            DESCENDING("desc");

            private String requestMapping;

            Direction(String requestMapping) {
                this.requestMapping = requestMapping;
            }

            /**
             * Converts the string representation of ordering used by DataTables
             * to this enum.
             *
             * @param requestMapping the string representation ("asc" or "desc")
             * @return the corresponding {@link Direction} value
             */
            public static Direction of(String requestMapping) {
                return Arrays.stream(values()).filter(e -> e.getRequestMapping().equals(requestMapping))
                        .findFirst().orElseThrow(IllegalArgumentException::new);
            }
        }
    }
}
