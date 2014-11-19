package com.sdl.webapp.main.controller;

import com.sdl.webapp.main.controller.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.main.controller.ControllerUtils.REQUEST_PATH_PREFIX;

/**
 * Default controller; this handles requests to unknown actions.
 */
@Controller
@RequestMapping(REQUEST_PATH_PREFIX)
public class DefaultController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultController.class);

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @RequestMapping(method = RequestMethod.GET, value = "/**")
    public String handleGetUnknownAction(HttpServletRequest request) {
        final String message = "Request to unknown action: " + urlPathHelper.getRequestUri(request);
        LOG.error(message);
        throw new BadRequestException(message);
    }
}
