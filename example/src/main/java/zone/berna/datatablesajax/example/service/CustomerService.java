package zone.berna.datatablesajax.example.service;

import com.github.javafaker.Faker;
import lombok.val;
import org.springframework.stereotype.Service;
import zone.berna.datatablesajax.example.model.Customer;
import zone.berna.datatablesajax.request.TableRequest;

import javax.validation.constraints.PositiveOrZero;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final Comparator<Customer> orderByName = Comparator.comparing(Customer::getName, String::compareToIgnoreCase);
    private static final Comparator<Customer> orderByEmail = Comparator.comparing(Customer::getEmail, String::compareToIgnoreCase);
    private static final Comparator<Customer> orderByAddress = Comparator.comparing(c -> c.getAddress().toString(), String::compareToIgnoreCase);
    private static final Map<String, Comparator<Customer>> comparators;

    static {
        comparators = new HashMap<>();
        comparators.put("name", orderByName);
        comparators.put("email", orderByEmail);
        comparators.put("address", orderByAddress);
    }

    private final List<Customer> customers;

    public CustomerService() {
        this.customers = generateMultiple(42);
    }

    private static Comparator<Customer> getComparator(String field, TableRequest.Order.Direction resultOrder) {
        val comparator = comparators.get(field);
        if (resultOrder.equals(TableRequest.Order.Direction.DESCENDING)) {
            return comparator.reversed();
        }
        return comparator;
    }

    private static Customer generate(int id) {
        val faker = new Faker();
        return Customer.builder()
                .id(id)
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .address(AddressService.generate()).build();
    }

    private static List<Customer> generateMultiple(@PositiveOrZero int qty) {
        if (qty < 0) {
            throw new IndexOutOfBoundsException();
        }
        val list = new ArrayList<Customer>();
        for (int i = 0; i < qty; i++) {
            list.add(generate(i));
        }
        return list;
    }

    public long getTotal() {
        return customers.size();
    }

    public long getTotalWithFilter(String query) {
        return customers.stream()
                .filter(c -> c.getName().toLowerCase().contains(query) ||
                        c.getEmail().toLowerCase().contains(query) ||
                        c.getAddress().toString().toLowerCase().contains(query))
                .count();
    }

    public List<Customer> getFilteredList(String query, String orderByField, TableRequest.Order.Direction direction, int start, int count) {
        val list = customers.stream()
                .filter(c -> c.getName().toLowerCase().contains(query) ||
                        c.getEmail().toLowerCase().contains(query) ||
                        c.getAddress().toString().toLowerCase().contains(query))
                .sorted(getComparator(orderByField, direction))
                .collect(Collectors.toList());
        return list.subList(start, Math.min(list.size(), start + count));
    }
}
