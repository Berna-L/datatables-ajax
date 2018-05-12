package zone.berna.datatablesajax.example.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class City {
    private String name;
    private State state;

    @Override
    public String toString() {
        return name + ", " + getState().toString();
    }
}
