package zone.berna.datatablesajax.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webjars.RequireJS;

/**
 * Controller to resolve Webjars resources.
 *
 * @see <a href="https://www.webjars.org/documentation#springboot">
 * https://www.webjars.org/documentation#springboot</a>
 */
@Controller
public class WebjarsController {

    @ResponseBody
    @RequestMapping(value = "/webjarsjs", produces = "application/javascript")
    public String webjarjs() {
        return RequireJS.getSetupJavaScript("/webjars/");
    }

}
