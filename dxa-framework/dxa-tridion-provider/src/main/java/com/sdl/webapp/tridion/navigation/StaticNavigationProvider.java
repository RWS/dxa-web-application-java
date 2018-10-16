package com.sdl.webapp.tridion.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.modelservice.service.ModelServiceProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link NavigationProvider} based on statically generated (published) <code>Navigation.json</code>.
 */
@Slf4j
@Service
public class StaticNavigationProvider implements NavigationProvider {

    static final String TYPE_STRUCTURE_GROUP = "StructureGroup";

    private final ObjectMapper objectMapper;

    private final LinkResolver linkResolver;

    private final ModelServiceProvider modelService;

    @Value("${dxa.tridion.navigation.modelUrl}")
    private String navigationModelUrl;

    @Autowired
    public StaticNavigationProvider(ObjectMapper objectMapper,
                                    LinkResolver linkResolver,
                                    ModelServiceProvider modelService) {
        this.objectMapper = objectMapper;
        this.linkResolver = linkResolver;
        this.modelService = modelService;
    }

    private static List<Link> createLinksForVisibleItems(Iterable<SitemapItem> items) {
        final List<Link> links = new ArrayList<>();
        for (SitemapItem item : items) {
            if (item.isVisible()) {
                links.add(linkForItem(item));
            }
        }
        return links;
    }

    private static SitemapItem findContextNavigationStructureGroup(SitemapItem item, String requestPath) {
        if (Objects.equals(item.getType(), TYPE_STRUCTURE_GROUP) && requestPath.startsWith(item.getUrl().toLowerCase())) {
            // Check if there is a matching subitem, if yes, then return it
            for (SitemapItem subItem : item.getItems()) {
                final SitemapItem matchingSubItem = findContextNavigationStructureGroup(subItem, requestPath);
                if (matchingSubItem != null) {
                    return matchingSubItem;
                }
            }

            // Otherwise return this matching item
            return item;
        }

        // No matching item
        return null;
    }

    private static boolean createBreadcrumbLinks(SitemapItem item, String requestPath, List<Link> links) {
        if (requestPath.startsWith(item.getUrl().toLowerCase())) {
            // Add link for this matching item
            links.add(linkForItem(item));

            // Add links for the following matching subitems
            // first element was just added, skip it
            Iterator<SitemapItem> iterator = sitemapIteratorWithoutFirst(item);
            while (iterator.hasNext()) {
                if (createBreadcrumbLinks(iterator.next(), requestPath, links)) {
                    return true;
                }
            }
            return true;
        }

        return false;
    }

    @NotNull
    private static Iterator<SitemapItem> sitemapIteratorWithoutFirst(SitemapItem item) {
        if (item.getItems().isEmpty()) {
            return Collections.<SitemapItem>emptyList().iterator();
        } else {
            Iterator<SitemapItem> iterator = item.getItems().iterator();
            if (iterator.hasNext()) {
                iterator.next();
            }
            return iterator;
        }
    }

    private static Link linkForItem(SitemapItem item) {
        Link link = new Link();
        link.setUrl(item.getUrl());
        link.setLinkText(item.getTitle());
        return link;
    }

    @Override
    public SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException {
        try {
            final String path = localization.localizePath(navigationModelUrl);

            return resolveLinks(objectMapper.readValue(getPageContent(path, localization), SitemapItem.class), localization);
        } catch (ContentProviderException | IOException e) {
            throw new NavigationProviderException("Exception while loading navigation model", e);
        }
    }

    @Override
    public NavigationLinks getTopNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);

        return new NavigationLinks(createLinksForVisibleItems(navigationModel.getItems()));
    }

    @Override
    public NavigationLinks getContextNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);
        final SitemapItem contextNavigationItem = findContextNavigationStructureGroup(navigationModel, requestPath);

        final List<Link> links = contextNavigationItem != null ? createLinksForVisibleItems(contextNavigationItem.getItems()) : Collections.emptyList();

        return new NavigationLinks(links);
    }

    @Override
    public NavigationLinks getBreadcrumbNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException {
        final SitemapItem navigationModel = getNavigationModel(localization);

        final List<Link> links = new ArrayList<>();
        createBreadcrumbLinks(navigationModel, requestPath, links);

        return new NavigationLinks(links);
    }

    private SitemapItem resolveLinks(SitemapItem sitemapItem, Localization localization) {
        sitemapItem.setUrl(linkResolver.resolveLink(sitemapItem.getUrl(), localization.getId()));

        for (SitemapItem subItem : sitemapItem.getItems()) {
            resolveLinks(subItem, localization);
        }

        return sitemapItem;
    }

    protected InputStream getPageContent(String path, Localization localization) throws ContentProviderException {
        String pageContent = modelService.loadPageContent(PageRequestDto.builder(localization.getId(), path).build());
        // NOTE: This assumes page content is always in UTF-8 encoding
        return new ByteArrayInputStream(pageContent.getBytes(StandardCharsets.UTF_8));
    }
}
