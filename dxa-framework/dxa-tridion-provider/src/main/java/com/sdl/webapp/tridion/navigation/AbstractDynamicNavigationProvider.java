package com.sdl.webapp.tridion.navigation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.NavigationProvider;
import com.sdl.webapp.common.api.content.NavigationProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Navigation Provider implementation based on Taxonomies (Categories &amp; Keywords).
 * <p>Falls back to {@link StaticNavigationProvider} when dynamic navigation is not available.</p>
 */
@Slf4j
public abstract class AbstractDynamicNavigationProvider implements NavigationProvider {

    private final StaticNavigationProvider staticNavigationProvider;

    private final LinkResolver linkResolver;

    @Value("${dxa.tridion.navigation.taxonomy.marker}")
    protected String taxonomyNavigationMarker;

    @Value("${dxa.tridion.navigation.taxonomy.type.taxonomyNode}")
    protected String sitemapItemTypeTaxonomyNode;

    @Value("${dxa.tridion.navigation.taxonomy.type.structureGroup}")
    protected String sitemapItemTypeStructureGroup;

    @Value("${dxa.tridion.navigation.taxonomy.type.page}")
    protected String sitemapItemTypePage;

    @Autowired
    public AbstractDynamicNavigationProvider(StaticNavigationProvider staticNavigationProvider, LinkResolver linkResolver) {
        this.staticNavigationProvider = staticNavigationProvider;
        this.linkResolver = linkResolver;
    }

    @Override
    public SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException {
        String taxonomyId = getNavigationTaxonomyIdInternal(localization);
        if (isFallbackNeeded(taxonomyId, localization)) {
            return staticNavigationProvider.getNavigationModel(localization);
        }

        return createTaxonomyNode(taxonomyId, localization);
    }

    @Override
    public NavigationLinks getTopNavigationLinks(String requestPath, final Localization localization) throws NavigationProviderException {
        String taxonomyId = getNavigationTaxonomyIdInternal(localization);
        if (isFallbackNeeded(taxonomyId, localization)) {
            return staticNavigationProvider.getTopNavigationLinks(requestPath, localization);
        }

        Collection<SitemapItem> items = filter(getTopNavigationLinksInternal(taxonomyId, localization), new Predicate<SitemapItem>() {
            @Override
            public boolean apply(SitemapItem input) {
                return input.isVisible() && !isNullOrEmpty(input.getUrl());
            }
        });
        return new NavigationLinks(Lists.newArrayList(transform(items, new Function<SitemapItem, Link>() {
            @Override
            public Link apply(SitemapItem input) {
                return input.createLink(linkResolver, localization);
            }
        })));
    }

    @Override
    public NavigationLinks getContextNavigationLinks(String requestPath, Localization localization) throws NavigationProviderException {
        return null;
    }

    @Override
    public NavigationLinks getBreadcrumbNavigationLinks(String requestPath, Localization localization) throws NavigationProviderException {
        return null;
    }

    protected String findIndexPageUrl(@NonNull List<SitemapItem> pageSitemapItems) {
        //noinspection StaticPseudoFunctionalStyleMethod
        SitemapItem index = Iterables.find(pageSitemapItems, new Predicate<SitemapItem>() {
            @Override
            public boolean apply(SitemapItem input) {
                return input.getUrl().endsWith("/index.html");
            }
        }, null);

        return index != null ? index.getUrl() : null;
    }

    private boolean isFallbackNeeded(String taxonomyId, Localization localization) {
        if (isEmpty(taxonomyId)) {
            log.debug("Taxonomy ID is empty or null for localization {}, fallback to static navigation is needed", taxonomyId, localization);
            return true;
        }
        return false;
    }

    private String getNavigationTaxonomyIdInternal(Localization localization) {
        String taxonomyId = getNavigationTaxonomyId(localization);
        log.trace("Taxonomy ID is {} for localization {}", taxonomyId, localization);
        return taxonomyId;
    }

    protected abstract List<SitemapItem> getTopNavigationLinksInternal(String taxonomyId, Localization localization);

    protected abstract SitemapItem createTaxonomyNode(String taxonomyId, Localization localization);

    protected abstract String getNavigationTaxonomyId(Localization localization);
}
