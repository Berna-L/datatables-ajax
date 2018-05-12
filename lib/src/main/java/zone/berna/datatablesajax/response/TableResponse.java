package zone.berna.datatablesajax.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * The object that represents a response of a request for data from DataTables.
 * <p>
 * Usually there's no need to read properties of this class,
 * since all manipulation is done by {@link zone.berna.datatablesajax.TableResponseGenerator TableResponseGenerator}.
 */
@Getter
@Builder
@EqualsAndHashCode
public class TableResponse {

    /**
     * Index of the request. Used by DataTables so a request whose response
     * is received after a later request is ignored.
     */
    private long draw;
    /**
     * Total of records, without any filtering.
     */
    private long recordsTotal;
    /**
     * Total of records matching the query.
     */
    private long recordsFiltered;
    /**
     * List of rows to be rendered.
     */
    @NonNull
    private List<List<String>> data;
}
