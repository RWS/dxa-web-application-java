package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.markup.Markup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

import static com.sdl.webapp.common.controller.ControllerUtils.SERVER_ERROR_VIEW;
import static com.sdl.webapp.common.controller.RequestAttributeNames.MARKUP;

/**
 * Controller which returns the sitemap in XML format.
 *
 * @dxa.publicApi
 */
@Controller
public class SiteMapXmlController {

    private static final Logger LOG = LoggerFactory.getLogger(SiteMapXmlController.class);

    private final WebRequestContext webRequestContext;

    private final NavigationProvider navigationProvider;

    private final Markup markup;

    @Autowired
    public SiteMapXmlController(WebRequestContext webRequestContext, NavigationProvider navigationProvider,
                                Markup markup) {
        this.webRequestContext = webRequestContext;
        this.navigationProvider = navigationProvider;
        this.markup = markup;
    }

    private static void writeSitemapItemsXml(Collection<SitemapItem> items, StringBuilder builder, String baseUrl) {
        for (SitemapItem item : items) {
            if ("Page".equals(item.getType()) && item.getUrl().startsWith("/")) {
                builder.append("<url>");
                builder.append("<loc>").append(baseUrl).append(item.getUrl()).append("</loc>");
                if (item.getPublishedDate() != null) {
                    builder.append("<lastmod>").append(item.getPublishedDate()).append("</lastmod>");
                }
                builder.append("</url>");
            } else {
                writeSitemapItemsXml(item.getItems(), builder, baseUrl);
            }
        }
    }

    /**
     * Handles a request for the sitemap in XML format.
     *
     * @throws NavigationProviderException If an error occurs so that the navigation data cannot be retrieved.
     */
    @RequestMapping(value = {"/sitemap.xml", "/{path}/sitemap.xml"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String handleGetSiteMapXml(HttpServletResponse response) throws NavigationProviderException {
        LOG.trace("handleGetSiteMapXml");

        final SitemapItem navigationModel = navigationProvider.getNavigationModel(webRequestContext.getLocalization());

        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        writeSitemapItemsXml(navigationModel.getItems(), builder, webRequestContext.getBaseUrl());
        builder.append("</urlset>");

        return builder.toString();
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        request.setAttribute(MARKUP, markup);
        return SERVER_ERROR_VIEW;
    }
}
