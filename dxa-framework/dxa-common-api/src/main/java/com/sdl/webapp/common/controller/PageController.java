package com.sdl.webapp.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.NavigationProvider;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.formats.DataFormatter;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationNotResolvedException;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.controller.exception.BadRequestException;
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import com.sdl.webapp.common.controller.exception.NotFoundException;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.Markup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;

import static com.sdl.webapp.common.controller.ControllerUtils.INCLUDE_PATH_PREFIX;
import static com.sdl.webapp.common.controller.ControllerUtils.SECTION_ERROR_VIEW;
import static com.sdl.webapp.common.controller.ControllerUtils.SERVER_ERROR_VIEW;
import static com.sdl.webapp.common.controller.RequestAttributeNames.CONTEXTENGINE;
import static com.sdl.webapp.common.controller.RequestAttributeNames.LOCALIZATION;
import static com.sdl.webapp.common.controller.RequestAttributeNames.MARKUP;
import static com.sdl.webapp.common.controller.RequestAttributeNames.MEDIAHELPER;
import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_ID;
import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;
import static com.sdl.webapp.common.controller.RequestAttributeNames.SCREEN_WIDTH;
import static com.sdl.webapp.common.controller.RequestAttributeNames.SOCIALSHARE_URL;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Main controller. This handles requests that come from the client.
 */
@Controller
@Slf4j
//todo dxa2 create error controller for error handling
public class PageController extends BaseController {

    // TODO: Move this to common-impl or core-module

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Autowired
    private ContentProvider contentProvider;

    @Autowired
    private LinkResolver linkResolver;

    @Autowired
    private MediaHelper mediaHelper;

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private Markup markup;

    @Autowired
    private DataFormatter dataFormatters;

    @Value("#{environment.getProperty('AllowJsonResponse', 'false')}")
    private boolean allowJsonResponse;

    @Autowired
    private NavigationProvider navigationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private static boolean isIncludeRequest(HttpServletRequest request) {
        return request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }

    /**
     * Gets a page requested by a client. This is the main handler method which gets called when a client sends a
     * request for a page.
     *
     * @param request  The request.
     * @param response the response
     * @return The view name of the page.
     * @throws java.lang.Exception exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "/**", produces = {MediaType.TEXT_HTML_VALUE, MediaType.ALL_VALUE})
    public String handleGetPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String requestPath = webRequestContext.getRequestPath();
        log.trace("handleGetPage: requestPath={}", requestPath);

        final Localization localization = webRequestContext.getLocalization();

        final PageModel originalPageModel = getPageModel(requestPath, localization);
        final ViewModel enrichedPageModel = enrichModel(originalPageModel, request);
        final PageModel page = enrichedPageModel instanceof PageModel ? (PageModel) enrichedPageModel : originalPageModel;

        if (page instanceof PageModel.WithResponseData) {
            if (((PageModel.WithResponseData) page).setResponseData(response)) {
                log.debug("Page {} has modified the HttpServletResponse object", page);
            }
        }

        log.trace("handleGetPage: page={}", page);

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
        log.trace("Page MvcData: {}", mvcData);

        return this.viewNameResolver.resolveView(mvcData, "Page");
    }

    /**
     * <p>handleGetPageFormatted.</p>
     *
     * @param request a {@link javax.servlet.http.HttpServletRequest} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/**", params = {"format"},
            produces = {"application/rss+xml", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
    public ModelAndView handleGetPageFormatted(final HttpServletRequest request) {

        final String requestPath = webRequestContext.getRequestPath();
        log.trace("handleGetPageFormatted: requestPath={}", requestPath);

        final Localization localization = webRequestContext.getLocalization();
        final PageModel page = getPageModel(requestPath, localization);
        enrichEmbeddedModels(page, request);
        log.trace("handleGetPageFormatted: page={}", page);
        return dataFormatters.view(page);
    }

    /**
     * <p>handleResolve.</p>
     *
     * @param itemId         a {@link java.lang.String} object.
     * @param localizationId a {@link java.lang.String} object.
     * @param defaultPath    a {@link java.lang.String} object.
     * @param defaultItem    a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/resolve/{itemId}")
    public String handleResolve(@PathVariable String itemId, @RequestParam String localizationId,
                                @RequestParam(required = false) String defaultPath,
                                @RequestParam(required = false) String defaultItem) throws DxaException {
        if (StringUtils.isEmpty(localizationId)) {
            throw new DxaException(new IllegalArgumentException("Localization ID is null while it shouldn't be"));
        }

        String url = linkResolver.resolveLink(itemId, localizationId);
        if (StringUtils.isEmpty(url) && !StringUtils.isEmpty(defaultItem)) {
            url = linkResolver.resolveLink(defaultItem, localizationId);
        }
        if (StringUtils.isEmpty(url)) {
            url = StringUtils.isEmpty(defaultPath) ? "/" : defaultPath;
        }
        return "redirect:" + url;
    }

    // Blank page for XPM

    /**
     * <p>handleResolveLoc.</p>
     *
     * @param itemId         a {@link java.lang.String} object.
     * @param localizationId a {@link java.lang.String} object.
     * @param defaultPath    a {@link java.lang.String} object.
     * @param defaultItem    a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{locPath}/resolve/{itemId}")
    public String handleResolveLoc(@PathVariable String itemId,
                                   @RequestParam String localizationId, @RequestParam String defaultPath,
                                   @RequestParam(required = false) String defaultItem) throws DxaException {
        return handleResolve(itemId, localizationId, defaultPath, defaultItem);
    }

    /**
     * <p>blankPage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/se_blank.html", produces = "text/html")
    @ResponseBody
    public String blankPage() {
        return "";
    }

    /**
     * <p>handleGetNavigationJson.</p>
     *
     * @return a {@link java.lang.String} object.
     * @throws com.sdl.webapp.common.api.content.NavigationProviderException if any.
     * @throws com.fasterxml.jackson.core.JsonProcessingException            if any.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/navigation.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String handleGetNavigationJson() throws NavigationProviderException, JsonProcessingException {
        log.trace("handleGetNavigationJson");

        SitemapItem model = navigationProvider.getNavigationModel(webRequestContext.getLocalization());

        return objectMapper.writeValueAsString(model);
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
     * @param request  The request.
     * @param response response
     * @return The name of the view that renders the "not found" page.
     * @throws java.lang.Exception exception
     */
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO TSI-775: No need to prefix with WebRequestContext.Localization.Path here (?)
        String path = webRequestContext.getLocalization().getPath();
        String notFoundPageUrl = (path.endsWith("/") ? path : path + '/') + "error-404";

        PageModel originalPageModel;
        try {
            originalPageModel = contentProvider.getPageModel(notFoundPageUrl, webRequestContext.getLocalization());
        } catch (ContentProviderException e) {
            log.error("Could not find error page", e);
            throw new HTTPException(SC_NOT_FOUND);
        }

        final ViewModel enrichedPageModel = enrichModel(originalPageModel, request);
        final PageModel pageModel = enrichedPageModel instanceof PageModel ? (PageModel) enrichedPageModel : originalPageModel;

        if (!isIncludeRequest(request)) {
            request.setAttribute(PAGE_ID, pageModel.getId());
        }

        request.setAttribute(PAGE_MODEL, pageModel);
        request.setAttribute(LOCALIZATION, webRequestContext.getLocalization());
        request.setAttribute(MARKUP, markup);
        request.setAttribute(MEDIAHELPER, mediaHelper);
        request.setAttribute(SCREEN_WIDTH, mediaHelper.getScreenWidth());
        request.setAttribute(SOCIALSHARE_URL, webRequestContext.getFullUrl());
        request.setAttribute(CONTEXTENGINE, webRequestContext.getContextEngine());

        response.setStatus(SC_NOT_FOUND);
        return this.viewNameResolver.resolveView(pageModel.getMvcData(), "Page");
    }

    @ExceptionHandler({LocalizationNotResolvedException.class})
    public void handleLocalizationNotResolvedException(HttpServletRequest request, HttpServletResponse response,
                                                       LocalizationNotResolvedException exception) throws IOException {
        log.error("Failed to retrieve localization for request url = {}, uri = {}",
                request.getRequestURL(), request.getRequestURI(), exception);
        if (exception instanceof LocalizationNotResolvedException.WithCustomResponse) {
            LocalizationNotResolvedException.WithCustomResponse custom =
                    (LocalizationNotResolvedException.WithCustomResponse) exception;
            writeResponse(response, custom.getHttpStatus(), custom.getContent(), custom.getContentType());
        } else {
            if (exception.getHttpStatus() >= 400) {
                response.sendError(exception.getHttpStatus(), exception.getMessage());
            } else {
                writeResponse(response, exception.getHttpStatus(), exception.getMessage(), "text/plain");
            }
        }
    }

    private void writeResponse(HttpServletResponse response, int httpStatus, String content, String contentType) throws IOException {
        response.resetBuffer();
        response.setStatus(httpStatus);
        response.getWriter().write(content);
        response.setContentType(contentType);
        response.flushBuffer();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handles non-specific exceptions.
     */
    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        log.error("Exception while processing request for: {}", urlPathHelper.getRequestUri(request), exception);
        request.setAttribute(MARKUP, markup);
        return isIncludeRequest(request) ? SECTION_ERROR_VIEW : SERVER_ERROR_VIEW;
    }

    private PageModel getPageModel(String path, Localization localization) {
        try {
            return contentProvider.getPageModel(path, localization);
        } catch (PageNotFoundException e) {
            log.error("Page not found: {}", path, e);
            throw new NotFoundException("Page not found: " + path, e);
        } catch (ContentProviderException e) {
            log.error("An unexpected error occurred", e);
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }

    /**
     * Enriches all the Region/Entity Models embedded in the given Page Model.
     * Used by <see cref="FormatDataAttribute"/> to get all embedded Models enriched without rendering any Views.
     *
     * @param model   The Page Model to enrich.
     * @param request http request
     */
    private void enrichEmbeddedModels(PageModel model, HttpServletRequest request) {
        if (model == null) {
            return;
        }

        for (RegionModel region : model.getRegions()) {
            // NOTE: Currently not enriching the Region Model itself, because we don't support custom Region Controllers (yet).
            for (int i = 0; i < region.getEntities().size(); i++) {
                EntityModel entity = region.getEntities().get(i);
                if (entity != null && entity.getMvcData() != null) {
                    region.getEntities().set(i, enrichEntityModel(entity, request));
                }
            }
        }
    }
}
