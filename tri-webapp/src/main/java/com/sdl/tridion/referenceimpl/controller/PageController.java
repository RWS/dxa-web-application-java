package com.sdl.tridion.referenceimpl.controller;

import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.core.factories.PageFactory;
import org.dd4t.core.request.impl.BasicRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class PageController {

    // TODO: Publication id should be determined from configuration instead of being hard-coded
    private static final int PUBLICATION_ID = 48;

    @Autowired
    private PageFactory pageFactory;

    @RequestMapping(method = GET, value = "*.html")
    public String handleRequest(HttpServletRequest request)
            throws NotAuthorizedException, NotAuthenticatedException, ItemNotFoundException {
        String uri = request.getRequestURI();
        uri = uri.replaceFirst(request.getContextPath(), "");

        final Page page = pageFactory.findPageByUrl(uri, PUBLICATION_ID, new BasicRequestContext(request));
        request.setAttribute("pageModel", page);

        return "page/GeneralPage";
    }
}
