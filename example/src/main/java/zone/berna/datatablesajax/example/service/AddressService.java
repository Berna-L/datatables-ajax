package zone.berna.datatablesajax.example.service;

import com.github.javafaker.Faker;
import lombok.val;
import zone.berna.datatablesajax.example.model.Address;
import zone.berna.datatablesajax.example.model.City;
import zone.berna.datatablesajax.example.model.State;

class AddressService {
    static Address generate() {
        val address = new Faker().address();
        return Address.builder()
                .street(address.streetName())
                .number(address.buildingNumber())
                .city(City.builder()
                        .name(address.cityName())
                        .state(State.builder()
                                .name(address.state())
                                .abbr(address.stateAbbr())
                                .build())
                        .build())
                .build();
    }
}
