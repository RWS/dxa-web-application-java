package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.*;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.main.controller.exception.InternalServerErrorException;
import com.sdl.webapp.main.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.main.RequestAttributeNames.PAGE_MODEL;
import static com.sdl.webapp.main.RequestAttributeNames.SCREEN_WIDTH;

@Controller
public class PageController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Autowired
    private ContentProvider contentProvider;

    @Autowired
    private WebRequestContext webRequestContext;

    @RequestMapping(method = RequestMethod.GET, value = "/**")
    public String handleGetPage(HttpServletRequest request) {
        // Strip the protocol, domain, port and context path off of the URL
        final String url = urlPathHelper.getRequestUri(request).replace(urlPathHelper.getContextPath(request), "");
        LOG.trace("handleGetPage: url={}", url);

        final Page page = getPageFromContentProvider(url, webRequestContext.getLocalization());
        LOG.trace("handleGetPage: page={}", page);

        request.setAttribute(PAGE_MODEL, page);
        request.setAttribute(SCREEN_WIDTH, webRequestContext.getScreenWidth());

        final String viewName = page.getViewName();
        LOG.trace("viewName: {}", viewName);
        return viewName;
    }

    private Page getPageFromContentProvider(String url, Localization localization) {
        try {
            return contentProvider.getPageModel(url, localization);
        } catch (PageNotFoundException e) {
            LOG.error("Page not found: {}", url, e);
            throw new NotFoundException("Page not found: " + url, e);
        } catch (ContentProviderException e) {
            LOG.error("An unexpected error occurred", e);
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }
}
