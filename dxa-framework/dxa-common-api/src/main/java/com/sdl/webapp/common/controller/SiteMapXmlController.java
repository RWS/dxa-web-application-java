package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.markup.Markup;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

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
    private static final int CAPACITY = 16384;

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

    static String getFormattedDateTime(DateTime moment) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(moment.toDate());
    }

    private static void writeSitemapItemsXml(Collection<SitemapItem> items, StringBuilder builder, String baseUrl) {
        for (SitemapItem item : items) {
            if (!"Page".equals(item.getType()) || !item.getUrl().startsWith("/")) {
                writeSitemapItemsXml(item.getItems(), builder, baseUrl);
                continue;
            }
            builder.append("<url>");
            builder.append("<loc>").append(baseUrl).append(item.getUrl()).append("</loc>");
            if (item.getPublishedDate() != null) {
                builder.append("<lastmod>").append(getFormattedDateTime(item.getPublishedDate())).append("</lastmod>");
            }
            builder.append("</url>");
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

        return getXmlSitemap();
    }

    @NotNull
    /**
     * This method is supposed to be extended in order to change the default behavior.
     */
    public String getXmlSitemap() throws NavigationProviderException {
        SitemapItem model = navigationProvider.getNavigationModel(webRequestContext.getLocalization());

        StringBuilder builder = new StringBuilder(CAPACITY);
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        writeSitemapItemsXml(model.getItems(), builder, webRequestContext.getBaseUrl());
        builder.append("</urlset>");

        return builder.toString();
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception exception) {
        LOG.error("Could not process sitemap.xml", exception);
        request.setAttribute(MARKUP, markup);
        return SERVER_ERROR_VIEW;
    }
}
