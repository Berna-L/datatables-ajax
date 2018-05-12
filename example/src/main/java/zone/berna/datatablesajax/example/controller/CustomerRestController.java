package zone.berna.datatablesajax.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zone.berna.datatablesajax.TableResponseGenerator;
import zone.berna.datatablesajax.example.service.CustomerService;
import zone.berna.datatablesajax.request.TableRequest;

/**
 * Simple REST controller to retrieve customer-related
 * objects.
 */
@RestController
@RequestMapping("api/customer")
public class CustomerRestController {

    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("get-table-data")
    public ResponseEntity<?> tableData(@RequestBody TableRequest tableRequest) {
        return ResponseEntity.ok(TableResponseGenerator.instance().generateResponse(tableRequest,
                customerService::getTotal,
                customerService::getTotalWithFilter,
                customerService::getFilteredList,
                "name",
                "email",
                "address"
        ));
    }
}
