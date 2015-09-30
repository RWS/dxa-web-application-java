package com.sdl.webapp.main.controller.core;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.controller.ViewResolver;
import com.sdl.webapp.common.controller.exception.BadRequestException;
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import com.sdl.webapp.common.controller.exception.NotFoundException;
import com.sdl.webapp.common.api.formats.DataFormatter;
import com.sdl.webapp.common.markup.Markup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.controller.RequestAttributeNames.*;
import static com.sdl.webapp.common.controller.ControllerUtils.*;

/**
 * Main controller. This handles requests that come from the client.
 */
@Controller
public class MainController {

    // TODO: Move this to common-impl or core-module

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Value("#{environment.getProperty('AllowJsonResponse', 'false')}")
    private boolean allowJsonResponse;

    private final ContentProvider contentProvider;

    private final LinkResolver linkResolver;

    private final MediaHelper mediaHelper;

    private final WebRequestContext webRequestContext;

    private final Markup markup;

    private final ViewResolver viewResolver;

    private final DataFormatter dataFormatter;

    @Autowired
    public MainController(ContentProvider contentProvider, LinkResolver linkResolver, MediaHelper mediaHelper,
                          WebRequestContext webRequestContext, Markup markup, ViewResolver viewResolver, DataFormatter dataFormatter) {
        this.contentProvider = contentProvider;
        this.linkResolver = linkResolver;
        this.mediaHelper = mediaHelper;
        this.webRequestContext = webRequestContext;
        this.markup = markup;
        this.viewResolver = viewResolver;
        this.dataFormatter = dataFormatter;
    }

    /**
     * Gets a page requested by a client. This is the main handler method which gets called when a client sends a
     * request for a page.
     *
     * @param request The request.
     * @return The view name of the page.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = {MediaType.TEXT_HTML_VALUE})
    public String handleGetPage(HttpServletRequest request) {
        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("handleGetPage: requestPath={}", requestPath);

        final Localization localization = webRequestContext.getLocalization();
        final PageModel page = getPageModel(requestPath, localization);
        LOG.trace("handleGetPage: page={}", page);

        if (!isIncludeRequest(request)) {
            request.setAttribute(PAGE_ID, page.getId());
        }

        request.setAttribute(PAGE_MODEL, page);
        request.setAttribute(LOCALIZATION, localization);
        request.setAttribute(MARKUP, markup);
        request.setAttribute(MEDIAHELPER, mediaHelper);
        request.setAttribute(SCREEN_WIDTH, mediaHelper.getScreenWidth());
        request.setAttribute(SOCIALSHARE_URL, webRequestContext.getFullUrl());
        request.setAttribute(CONTEXTENGINE, webRequestContext.getContextEngine());

        final MvcData mvcData = page.getMvcData();
        LOG.trace("Page MvcData: {}", mvcData);

        return this.viewResolver.resolveView(mvcData, "Page", request);
        //return mvcData.getAreaName() + "/Page/" + mvcData.getViewName();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = {"application/rss+xml", "application/json", "application/atom+xml"})
    public ModelAndView handleGetPageFormatted(HttpServletRequest request, @RequestParam(required = false) String format) {

        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("handleGetPageAsJson: requestPath={}", requestPath);

        final Localization localization = webRequestContext.getLocalization();

        //TODO: PageModel loads includes as regions in 1.1 (.net)
        final PageModel page = getPageModel(requestPath, localization);
        LOG.trace("handleGetPageAsJson: page={}", page);


        ModelAndView mav = new ModelAndView();
        switch (format){
            case "rss":
                mav.setViewName("rssFeedView");
                break;
            case "atom":
                mav.setViewName("atomFeedView");
                break;
            default:
                //json
                mav.setViewName("jsonFeedView");
                break;
        }
        mav.addObject("formatter", dataFormatter.getFormatter(format));
        mav.addObject("data", page);

        return mav;

    }



    @RequestMapping(method = RequestMethod.GET, value = "/resolve/{itemId}")
    public String handleResolve(@PathVariable String itemId, @RequestParam String localizationId,
                                @RequestParam(required = false) String defaultPath,
                                @RequestParam(required = false) String defaultItem) {
        String url = linkResolver.resolveLink(itemId, localizationId);
        if (Strings.isNullOrEmpty(url)) {
            url = linkResolver.resolveLink(defaultItem, localizationId);
        }
        if (Strings.isNullOrEmpty(url)) {
            url = Strings.isNullOrEmpty(defaultPath) ? "/" : defaultPath;
        }
        return "redirect:" + url;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{locPath}/resolve/{itemId}")
    public String handleResolveLoc(@PathVariable String locPath, @PathVariable String itemId,
                                   @RequestParam String localizationId, @RequestParam String defaultPath,
                                   @RequestParam(required = false) String defaultItem) {
        return handleResolve(itemId, localizationId, defaultPath, defaultItem);
    }

    // Blank page for XPM
    @RequestMapping(method = RequestMethod.GET, value = "/se_blank.html", produces = "text/html")
    @ResponseBody
    public String blankPage() {
        return "";
    }

    /**
     * Throws a {@code BadRequestException} when a request is made to an URL under /system/mvc which is not handled
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
        return isIncludeRequest(request) ? SECTION_ERROR_VIEW : NOT_FOUND_ERROR_VIEW;
    }

    /**
     * Handles non-specific exceptions.
     *
     * @param request   The request.
     * @param exception The exception.
     * @return The name of the view that renders the "internal server error" page.
     */
    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        LOG.error("Exception while processing request for: {}", urlPathHelper.getRequestUri(request), exception);
        request.setAttribute(MARKUP, markup);
        return isIncludeRequest(request) ? SECTION_ERROR_VIEW : SERVER_ERROR_VIEW;
    }

    private PageModel getPageModel(String path, Localization localization) {
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

    private boolean isIncludeRequest(HttpServletRequest request) {
        return request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }
}
