package com.sdl.webapp.common.impl.content;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.NavigationBuilder;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Must be refactored.
 * NOTE: "StructureGroup" is a Tridion-specific thing. This should probably be in webapp-tridion.
 */
@Component
public class NavigationBuilderImpl implements NavigationBuilder {

    private final ContentProvider contentProvider;

    @Autowired
    public NavigationBuilderImpl(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    @Override
    public NavigationLinks buildContextNavigation(String requestPath, Localization localization) throws ContentProviderException {
        SitemapItem parent = contentProvider.getNavigationModel(localization);
        int levels = requestPath.split("/").length;
        while (levels > 1 && parent.getItems() != null) {
            SitemapItem newParent = findStructureGroupWithUrl(parent.getItems(), requestPath);
            if (newParent == null) {
                break;
            }
            parent = newParent;
        }

        final List<Link> links;
        if (parent != null && parent.getItems() != null) {
            links = createLinks(parent.getItems());
        } else {
            links = Collections.emptyList();
        }

        final NavigationLinks navigationLinks = new NavigationLinks();
        navigationLinks.setItems(links);
        return navigationLinks;
    }

    private SitemapItem findStructureGroupWithUrl(Iterable<SitemapItem> items, String requestPath) {
        for (SitemapItem item : items) {
            if (item.getType().equals("StructureGroup") && requestPath.startsWith(item.getUrl().toLowerCase())) {
                return item;
            }
        }
        return null;
    }

    @Override
    public NavigationLinks buildBreadcrumb(String requestPath, Localization localization) throws ContentProviderException {
        final SitemapItem navigationModel = contentProvider.getNavigationModel(localization);

        final List<Link> links = new ArrayList<>();
        // TODO: Not yet implemented

        final NavigationLinks navigationLinks = new NavigationLinks();
        navigationLinks.setItems(links);
        return navigationLinks;
    }

    @Override
    public NavigationLinks buildTopNavigation(String requestPath, Localization localization) throws ContentProviderException {
        final SitemapItem navigationModel = contentProvider.getNavigationModel(localization);

        final NavigationLinks navigationLinks = new NavigationLinks();
        navigationLinks.setItems(createLinks(navigationModel.getItems()));
        return navigationLinks;
    }

    private List<Link> createLinks(Iterable<SitemapItem> items) {
        final List<Link> links = new ArrayList<>();
        for (SitemapItem item : items) {
            if (item.isVisible()) {
                links.add(createLink(item));
            }
        }
        return links;
    }

    private Link createLink(SitemapItem item) {
        Link link = new Link();
        link.setUrl(item.getUrl());  // TODO: Resolve link
        link.setLinkText(item.getTitle());
        return link;
    }
}
