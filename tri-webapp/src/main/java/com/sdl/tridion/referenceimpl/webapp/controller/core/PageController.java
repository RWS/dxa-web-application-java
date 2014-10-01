package com.sdl.tridion.referenceimpl.webapp.controller.core;

import com.sdl.tridion.referenceimpl.common.ContentProvider;
import com.sdl.tridion.referenceimpl.common.ContentProviderException;
import com.sdl.tridion.referenceimpl.common.PageNotFoundException;
import com.sdl.tridion.referenceimpl.common.config.ScreenWidth;
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

@Controller
public class PageController {
    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    @Autowired
    private ContentProvider contentProvider;

    @RequestMapping(method = RequestMethod.GET, value = "/*")
    public String handleGetPage(HttpServletRequest request) {
        final String url = getPageUrl(request);
        LOG.debug("handleGetPage: url={}", url);

        final Page page = getPageFromContentProvider(url);
        LOG.debug("handleGetPage: page={}", page);

        request.setAttribute(ViewAttributeNames.PAGE_MODEL, page);

        // TODO: Set this with real data instead of hard-coded constant value
        request.setAttribute(ViewAttributeNames.SCREEN_WIDTH, ScreenWidth.MEDIUM);

        return page.getViewName();
    }

    private String getPageUrl(HttpServletRequest request) {
        String url = URL_PATH_HELPER.getRequestUri(request).replace(URL_PATH_HELPER.getContextPath(request), "");

        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        if (!url.endsWith(".html")) {
            url = url + ".html";
        }

        return url;
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
