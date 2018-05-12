package zone.berna.datatablesajax.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Simple view controller for the customer entity.
 */
@Controller
@RequestMapping("customer")
public class CustomerController {

    @GetMapping
    public ModelAndView page() {
        return new ModelAndView("table");
    }

}
