package com.sdl.webapp.common.impl.content;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.NavigationBuilder;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NavigationBuilderImpl implements NavigationBuilder {

    private final ContentProvider contentProvider;

    private final WebRequestContext webRequestContext;

    @Autowired
    public NavigationBuilderImpl(ContentProvider contentProvider, WebRequestContext webRequestContext) {
        this.contentProvider = contentProvider;
        this.webRequestContext = webRequestContext;
    }

    @Override
    public NavigationLinks buildContextNavigation() throws ContentProviderException {
        // TODO: Not yet implemented
        return null;
    }

    @Override
    public NavigationLinks buildBreadcrumb() throws ContentProviderException {
        // TODO: Not yet implemented
        return null;
    }

    @Override
    public NavigationLinks buildTopNavigation() throws ContentProviderException {
        final NavigationLinks navigationLinks = new NavigationLinks();

        final SitemapItem navigationModel = contentProvider.getNavigationModel(webRequestContext.getLocalization());

        final List<Link> links = new ArrayList<>();
        for (SitemapItem item : navigationModel.getItems()) {
            if (item.isVisible()) {
                Link link = new Link();
                link.setUrl(item.getUrl());  // TODO: Resolve link
                link.setLinkText(item.getTitle());
                links.add(link);
            }
        }

        navigationLinks.setItems(links);
        return navigationLinks;
    }
}
