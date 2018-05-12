package zone.berna.datatablesajax.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableResponseTest {

    @Test
    void checkBuilder() {
        final long draw = 0;
        final long totalRecords = 10;
        final long filteredRecords = 5;
        final List<List<String>> data = Collections.singletonList(Collections.singletonList("test"));
        TableResponse tr = TableResponse.builder()
                .draw(draw)
                .recordsTotal(totalRecords)
                .recordsFiltered(filteredRecords)
                .data(data)
                .build();
        assertEquals(draw, tr.getDraw());
        assertEquals(totalRecords, tr.getRecordsTotal());
        assertEquals(filteredRecords, tr.getRecordsFiltered());
        assertIterableEquals(data, tr.getData());
    }

    @Test
    void checkBuilderNullData() {
        Executable e = () -> TableResponse.builder().data(null).build();
        assertThrows(NullPointerException.class, e);
    }
}