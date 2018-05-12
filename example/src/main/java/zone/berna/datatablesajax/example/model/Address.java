package zone.berna.datatablesajax.example.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String street;
    private String number;
    private City city;

    @Override
    public String toString() {
        return street +
                " " +
                number +
                "\n" +
                getCity().toString();
    }
}