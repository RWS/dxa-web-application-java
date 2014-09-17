package com.sdl.tridion.referenceimpl.controller;

import com.sdl.tridion.referenceimpl.model.ContentProvider;
import com.sdl.tridion.referenceimpl.model.PageModel;
import com.sdl.tridion.referenceimpl.model.PageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController {
    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

    private static final String PAGE_VIEW_PREFIX = "page/";

    @Autowired
    private ContentProvider contentProvider;

    @RequestMapping(method = RequestMethod.GET, value = "/*")
    public String handleGetPage(HttpServletRequest request) {
        final String uri = request.getRequestURI().replaceFirst(request.getContextPath(), "");
        LOG.debug("handleGetPage: uri={}", uri);

        final PageModel pageModel = getPageModel(uri);
        request.setAttribute(JspBeanNames.PAGE_MODEL, pageModel);

        return PAGE_VIEW_PREFIX + pageModel.getViewName();
    }

    private PageModel getPageModel(String uri) {
        try {
            return contentProvider.getPageModel(uri);
        } catch (PageNotFoundException e) {
            throw new NotFoundException("Page not found: " + uri, e);
        }
    }
}
