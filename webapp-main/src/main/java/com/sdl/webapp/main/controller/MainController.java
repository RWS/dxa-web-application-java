package com.sdl.webapp.main.controller;

import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.util.StreamUtils;
import com.sdl.webapp.main.controller.exception.BadRequestException;
import com.sdl.webapp.main.controller.exception.InternalServerErrorException;
import com.sdl.webapp.main.controller.exception.NotFoundException;
import com.sdl.webapp.main.markup.Markup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.sdl.webapp.main.RequestAttributeNames.*;
import static com.sdl.webapp.main.controller.ControllerUtils.INCLUDE_PATH_PREFIX;

/**
 * Main controller. This handles requests that come from the client.
 */
@Controller
public class MainController {
    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    private static final String ALLOW_JSON_RESPONSE_PROPERTY = "AllowJsonResponse";

    private static final String NOT_FOUND_ERROR_VIEW = "Shared/NotFoundError";
    private static final String SERVER_ERROR_VIEW = "Shared/ServerError";

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private final Environment environment;

    private final ContentProvider contentProvider;

    private final MediaHelper mediaHelper;

    private final WebRequestContext webRequestContext;

    private final Markup markup;

    @Autowired
    public MainController(Environment environment, ContentProvider contentProvider, MediaHelper mediaHelper,
                          WebRequestContext webRequestContext, Markup markup) {
        this.environment = environment;
        this.contentProvider = contentProvider;
        this.mediaHelper = mediaHelper;
        this.webRequestContext = webRequestContext;
        this.markup = markup;
    }

    /**
     * Gets a page requested by a client. This is the main handler method which gets called when a client sends a
     * request for a page.
     *
     * @param request The request.
     * @return The view name of the page.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = "text/html")
    public String handleGetPage(HttpServletRequest request) {
        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("handleGetPage: requestPath={}", requestPath);

        final Page page = getPageModel(requestPath, webRequestContext.getLocalization());
        LOG.trace("handleGetPage: page={}", page);

        request.setAttribute(PAGE_MODEL, page);
        request.setAttribute(MARKUP, markup);
        request.setAttribute(SCREEN_WIDTH, mediaHelper.getScreenWidth());

        final MvcData mvcData = page.getMvcData();
        LOG.trace("Page MvcData: {}", mvcData);
        return mvcData.getAreaName() + "/Page/" + mvcData.getViewName();
    }

    /**
     * Gets a page requested by a client in JSON format. For security reasons, this is only enabled if the system
     * property "AllowJsonResponse" is set to {@code true}; if not, a 406 Not Acceptable status code is returned.
     *
     * @param request The request.
     * @param response The response.
     * @throws IOException If an I/O error occurs.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = "application/json")
    public void handleGetPageJSON(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final ServletServerHttpResponse res = new ServletServerHttpResponse(response);

        // Only handle this if explicitly enabled (by an environment property)
        if (!environment.getProperty(ALLOW_JSON_RESPONSE_PROPERTY, Boolean.class, false)) {
            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            res.close();
            return;
        }

        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("handleGetPageJSON: requestPath={}", requestPath);

        res.setStatusCode(HttpStatus.OK);
        res.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        final Localization localization = webRequestContext.getLocalization();
        try (final InputStream in = getPageContent(requestPath, localization); final OutputStream out = res.getBody()) {
            StreamUtils.copy(in, out);
        }

        res.close();
    }

    // Blank page for XPM
    @RequestMapping(method = RequestMethod.GET, value = "/se_blank.html", produces = "text/html")
    @ResponseBody
    public String blankPage() {
        return "";
    }

    /**
     * Throws a {@code BadRequestException }when a request is made to an URL under /system/mvc which is not handled
     * by another controller.
     *
     * @param request The request.
     */
    @RequestMapping(method = RequestMethod.GET, value = INCLUDE_PATH_PREFIX + "**")
    public void handleGetUnknownAction(HttpServletRequest request) {
        throw new BadRequestException("Request to unknown action: " + urlPathHelper.getRequestUri(request));
    }

    /**
     * Handles a {@code NotFoundException}.
     *
     * @param request The request.
     * @return The name of the view that renders the "not found" page.
     */
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(HttpServletRequest request) {
        request.setAttribute(MARKUP, markup);
        return NOT_FOUND_ERROR_VIEW;
    }

    /**
     * Handles non-specific exceptions.
     *
     * @param request The request.
     * @param exception The exception.
     * @return The name of the view that renders the "internal server error" page.
     */
    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        LOG.error("Exception while processing request for: {}", urlPathHelper.getRequestUri(request), exception);
        request.setAttribute(MARKUP, markup);
        return SERVER_ERROR_VIEW;
    }

    private Page getPageModel(String path, Localization localization) {
        try {
            return contentProvider.getPageModel(path, localization);
        } catch (PageNotFoundException e) {
            LOG.error("Page not found: {}", path, e);
            throw new NotFoundException("Page not found: " + path, e);
        } catch (ContentProviderException e) {
            LOG.error("An unexpected error occurred", e);
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }

    private InputStream getPageContent(String path, Localization localization) {
        try {
            return contentProvider.getPageContent(path, localization);
        } catch (PageNotFoundException e) {
            LOG.error("Page not found: {}", path, e);
            throw new NotFoundException("Page not found: " + path, e);
        } catch (ContentProviderException e) {
            LOG.error("An unexpected error occurred", e);
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }
}
