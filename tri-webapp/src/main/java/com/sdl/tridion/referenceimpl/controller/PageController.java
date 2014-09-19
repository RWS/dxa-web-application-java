package com.sdl.tridion.referenceimpl.controller;

import com.sdl.tridion.referenceimpl.common.ContentProvider;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.PageNotFoundException;
import com.sdl.tridion.referenceimpl.controller.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController {
    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

    private static final String PAGE_VIEW_PREFIX = "page/";

    @Autowired
    private ContentProvider contentProvider;

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public String handleGetPage(HttpServletRequest request, @PathVariable("name") String name) {
        if (request.getRequestURI().replace(request.getContextPath(), "").equals("/favicon.ico")) {
            LOG.debug("Skipping request for /favicon.ico");
            return null;
        }

        LOG.debug("handleGetPage: name={}", name);

        final Page pageModel;
        try {
            pageModel = contentProvider.getPage("/" + name + ".html");
        } catch (PageNotFoundException e) {
            throw new NotFoundException("Page not found: " + name, e);
        }

        request.setAttribute(JspBeanNames.PAGE_MODEL, pageModel);

        LOG.debug("pageModel={}", pageModel);

        return PAGE_VIEW_PREFIX + pageModel.getViewName();
    }
}
