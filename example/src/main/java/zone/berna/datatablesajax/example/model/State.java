package zone.berna.datatablesajax.example.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class State {

    private String name;
    private String abbr;

    public String toString() {
        return name + " (" + abbr + ")";
    }
}
