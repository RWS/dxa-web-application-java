package com.sdl.webapp.common.controller;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.*;
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
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import static com.sdl.webapp.common.controller.RequestAttributeNames.*;
import static com.sdl.webapp.common.controller.ControllerUtils.*;

/**
 * Main controller. This handles requests that come from the client.
 */
@Controller
public class PageController extends BaseController {

    // TODO: Move this to common-impl or core-module

    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Value("#{environment.getProperty('AllowJsonResponse', 'false')}")
    private boolean allowJsonResponse;

    private final ContentProvider contentProvider;

    private final LinkResolver linkResolver;

    private final MediaHelper mediaHelper;

    private final WebRequestContext webRequestContext;

    private final Markup markup;

    private final ViewResolver viewResolver;

    private final DataFormatter dataFormatters;

    @Autowired
    public PageController(ContentProvider contentProvider, LinkResolver linkResolver, MediaHelper mediaHelper,
                          WebRequestContext webRequestContext, Markup markup, ViewResolver viewResolver, DataFormatter dataFormatter) {
        this.contentProvider = contentProvider;
        this.linkResolver = linkResolver;
        this.mediaHelper = mediaHelper;
        this.webRequestContext = webRequestContext;
        this.markup = markup;
        this.viewResolver = viewResolver;
        this.dataFormatters = dataFormatter;
    }

    /**
     * Gets a page requested by a client. This is the main handler method which gets called when a client sends a
     * request for a page.
     *
     * @param request The request.
     * @return The view name of the page.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = {MediaType.TEXT_HTML_VALUE})
    public String handleGetPage(HttpServletRequest request) throws Exception {
        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("handleGetPage: requestPath={}", requestPath);

        final Localization localization = webRequestContext.getLocalization();

        final PageModel originalPageModel = getPageModel(requestPath, localization);
        final ViewModel enrichedPageModel = enrichModel(originalPageModel);
        final PageModel page = enrichedPageModel instanceof PageModel ? (PageModel)enrichedPageModel:originalPageModel;

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
    }

    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = {"application/rss+xml", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
    public ModelAndView handleGetPageFormatted(HttpServletRequest request) {

        final String requestPath = webRequestContext.getRequestPath();
        LOG.trace("handleGetPageFormatted: requestPath={}", requestPath);

        final Localization localization = webRequestContext.getLocalization();
        final PageModel page = getPageModel(requestPath, localization);
        EnrichEmbeddedModels(page);
        LOG.trace("handleGetPageFormatted: page={}", page);
        return dataFormatters.view(page);
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
    public String handleNotFoundException(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String notFoundPageUrl = webRequestContext.getLocalization().getPath() + "/error-404"; // TODO TSI-775: No need to prefix with WebRequestContext.Localization.Path here (?)

        PageModel originalPageModel;
        try
        {
            originalPageModel = contentProvider.getPageModel(notFoundPageUrl, webRequestContext.getLocalization());
        }
        catch (ContentProviderException e) {
            LOG.error("Could not find error page", e);
            throw new HTTPException(404);
        }

        final ViewModel enrichedPageModel = enrichModel(originalPageModel);
        final PageModel pageModel = enrichedPageModel instanceof PageModel ? (PageModel)enrichedPageModel:originalPageModel;

        response.setStatus(404);
        return this.viewResolver.resolveView(pageModel.getMvcData(), "Page", request);
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

    /**
     * Enriches all the Region/Entity Models embedded in the given Page Model.
     * Used by <see cref="FormatDataAttribute"/> to get all embedded Models enriched without rendering any Views.
     * @param model   The Page Model to enrich.
     */
    void EnrichEmbeddedModels(PageModel model)
    {
            if (model == null)
            {
                return;
            }

            for (RegionModel region : model.getRegions())
            {
                // NOTE: Currently not enriching the Region Model itself, because we don't support custom Region Controllers (yet).
                for (int i = 0; i < region.getEntities().size(); i++)
                {
                    EntityModel entity = region.getEntities().get(i);
                    if (entity != null && entity.getMvcData() != null)
                    {
                        region.getEntities().set(i, enrichEntityModel(entity));
                    }
                }
            }
    }



    private boolean isIncludeRequest(HttpServletRequest request) {
        return request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }
}
