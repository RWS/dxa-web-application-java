package com.sdl.tridion.referenceimpl.webapp.controller.core;

import com.sdl.tridion.referenceimpl.common.ContentProvider;
import com.sdl.tridion.referenceimpl.common.ContentProviderException;
import com.sdl.tridion.referenceimpl.common.PageNotFoundException;
import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.webapp.controller.exception.InternalServerErrorException;
import com.sdl.tridion.referenceimpl.webapp.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.tridion.referenceimpl.webapp.WebAppConstants.PAGE_MODEL;
import static com.sdl.tridion.referenceimpl.webapp.WebAppConstants.SCREEN_WIDTH;

@Controller
public class PageController extends ControllerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    @Autowired
    private ContentProvider contentProvider;

    @Autowired
    private WebRequestContext webRequestContext;

    @RequestMapping(method = RequestMethod.GET, value = "/**")
    public String handleGetPage(HttpServletRequest request) {
        final String url = getPageUrl(request);
        LOG.debug("handleGetPage: url={}", url);

        final Page page = getPageFromContentProvider(url);
        LOG.debug("handleGetPage: page={}", page);

        request.setAttribute(PAGE_MODEL, page);
        request.setAttribute(SCREEN_WIDTH, webRequestContext.getScreenWidth());

        return page.getViewName();
    }

    private String getPageUrl(HttpServletRequest request) {
        return URL_PATH_HELPER.getRequestUri(request).replace(URL_PATH_HELPER.getContextPath(request), "");
    }

    private Page getPageFromContentProvider(String url) {
        try {
            return contentProvider.getPage(url);
        } catch (PageNotFoundException e) {
            LOG.error("Page not found: {}", url, e);
            throw new NotFoundException("Page not found: " + url, e);
        } catch (ContentProviderException e) {
            LOG.error("An unexpected error occurred", e);
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }
}
