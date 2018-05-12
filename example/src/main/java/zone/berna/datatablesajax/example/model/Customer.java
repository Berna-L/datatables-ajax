package zone.berna.datatablesajax.example.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class Customer {
    private int id;
    private String name;
    private Address address;
    @Email
    private String email;
}
