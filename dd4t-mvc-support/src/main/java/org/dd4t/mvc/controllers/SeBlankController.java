package org.dd4t.mvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
@Controller
public class SeBlankController {
    /**
     * Required for XPM - this does not have to return any particular content, just a valid response
     */
    @RequestMapping (value = {"/se_blank.html"}, method = {RequestMethod.GET, RequestMethod.HEAD, RequestMethod.POST})
    protected String seBlank (final HttpServletResponse response) throws IOException {
        response.setStatus(200);
        return null;
    }

}
