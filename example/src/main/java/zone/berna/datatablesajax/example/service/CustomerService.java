package zone.berna.datatablesajax.example.service;

import com.github.javafaker.Faker;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.logging.log4j.message.FormattedMessageFactory;
import org.springframework.stereotype.Service;
import zone.berna.datatablesajax.example.model.Customer;
import zone.berna.datatablesajax.request.TableRequest;

import javax.validation.constraints.PositiveOrZero;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Log4j2
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

    private final CompletableFuture<List<Customer>> customers;

    public CustomerService() {
        this.customers = CompletableFuture.supplyAsync(() -> generateMultiple(98));
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
        val before = Instant.now();
        log.info(() -> new FormattedMessageFactory().newMessage("Starting creation of {} customer entries...", qty));
        if (qty < 0) {
            throw new IndexOutOfBoundsException();
        }
        val list = new ArrayList<Customer>();
        for (int i = 0; i < qty; i++) {
            list.add(generate(i));
        }
        log.info(() -> new FormattedMessageFactory().newMessage("Creation complete! Time elapsed: {}", Duration.between(before, Instant.now())));
        return list;
    }

    private List<Customer> getCustomers() {
        try {
            return this.customers.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    public long getTotal() {
        return getCustomers().size();
    }

    public long getTotalWithFilter(String query) {
        return getCustomers().stream()
                .filter(c -> c.getName().toLowerCase().contains(query) ||
                        c.getEmail().toLowerCase().contains(query) ||
                        c.getAddress().toString().toLowerCase().contains(query))
                .count();
    }

    public List<Customer> getFilteredList(String query, String orderByField, TableRequest.Order.Direction direction, int start, int count) {
        val list = getCustomers().stream()
                .filter(c -> c.getName().toLowerCase().contains(query) ||
                        c.getEmail().toLowerCase().contains(query) ||
                        c.getAddress().toString().toLowerCase().contains(query))
                .sorted(getComparator(orderByField, direction))
                .collect(Collectors.toList());
        return list.subList(start, Math.min(list.size(), start + count));
    }
}
