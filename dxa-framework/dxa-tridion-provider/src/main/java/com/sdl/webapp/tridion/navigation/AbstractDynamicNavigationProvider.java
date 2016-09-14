package com.sdl.webapp.tridion.navigation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.util.LocalizationUtils;
import com.sdl.webapp.tridion.navigation.data.KeywordDTO;
import com.sdl.webapp.tridion.navigation.data.PageMetaDTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.providers.PayloadCacheProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.comparator.NullSafeComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.sdl.webapp.common.util.LocalizationUtils.isHomePath;
import static com.sdl.webapp.common.util.LocalizationUtils.isIndexPath;
import static com.sdl.webapp.common.util.LocalizationUtils.stripDefaultExtension;
import static com.sdl.webapp.common.util.LocalizationUtils.stripIndexPath;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.KEYWORD;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.PAGE;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.getTaxonomySitemapIdentifier;

/**
 * Navigation Provider implementation based on Taxonomies (Categories &amp; Keywords).
 * <p>Falls back to {@link StaticNavigationProvider} when dynamic navigation is not available.</p>
 */
@Slf4j
public abstract class AbstractDynamicNavigationProvider implements NavigationProvider {

    static final NullSafeComparator<SitemapItem> SITEMAP_SORT_BY_TITLE = new NullSafeComparator<>(new Comparator<SitemapItem>() {
        @Override
        public int compare(SitemapItem o1, SitemapItem o2) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
    }, true);

    private final StaticNavigationProvider staticNavigationProvider;

    private final LinkResolver linkResolver;

    private final PayloadCacheProvider cacheProvider;

    @Value("${dxa.tridion.navigation.taxonomy.marker}")
    protected String taxonomyNavigationMarker;

    @Value("${dxa.tridion.navigation.taxonomy.type.taxonomyNode}")
    protected String sitemapItemTypeTaxonomyNode;

    @Value("${dxa.tridion.navigation.taxonomy.type.structureGroup}")
    protected String sitemapItemTypeStructureGroup;

    @Value("${dxa.tridion.navigation.taxonomy.type.page}")
    protected String sitemapItemTypePage;

    @Autowired
    public AbstractDynamicNavigationProvider(StaticNavigationProvider staticNavigationProvider, LinkResolver linkResolver, PayloadCacheProvider cacheProvider) {
        this.staticNavigationProvider = staticNavigationProvider;
        this.linkResolver = linkResolver;
        this.cacheProvider = cacheProvider;
    }

    @Override
    public SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException {
        CacheElement<SitemapItem> navigationModel = cacheProvider.loadPayloadFromLocalCache(localization.getId());
        if (navigationModel.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (navigationModel) {
                if (navigationModel.isExpired()) {
                    navigationModel.setExpired(false);

                    String taxonomyId = getNavigationTaxonomyIdInternal(localization);
                    if (isFallbackRequired(taxonomyId, localization)) {
                        return staticNavigationProvider.getNavigationModel(localization);
                    }

                    navigationModel.setPayload(createTaxonomyNode(taxonomyId, localization));
                    log.debug("Put navigation model for taxonomy id {} for localization id {} in cache", taxonomyId, localization.getId());

                }
            }
        }

        return navigationModel.getPayload();
    }


    @Override
    public NavigationLinks getTopNavigationLinks(String requestPath, final Localization localization) throws NavigationProviderException {
        return processNavigationLinks(requestPath, localization, new NavigationProcessing() {
            @Override
            public List<SitemapItem> processNavigation(SitemapItem navigationModel) {
                return navigationModel.getItems();
            }

            @Override
            public NavigationLinks fallback(String requestPath, Localization localization) throws NavigationProviderException {
                return staticNavigationProvider.getTopNavigationLinks(requestPath, localization);
            }
        });
    }

    @Override
    public NavigationLinks getContextNavigationLinks(final String requestPath, Localization localization) throws NavigationProviderException {
        return processNavigationLinks(requestPath, localization, new NavigationProcessing() {
            @Override
            public List<SitemapItem> processNavigation(SitemapItem navigationModel) {
                SitemapItem currentLevel = navigationModel.findWithUrl(stripDefaultExtension(requestPath));

                if (currentLevel != null && !(currentLevel instanceof TaxonomyNode)) {
                    currentLevel = currentLevel.getParent();
                }

                return currentLevel == null || currentLevel.getItems() == null ? Collections.<SitemapItem>emptyList() : currentLevel.getItems();
            }

            @Override
            public NavigationLinks fallback(String requestPath, Localization localization) throws NavigationProviderException {
                return staticNavigationProvider.getContextNavigationLinks(requestPath, localization);
            }
        });
    }

    @Override
    public NavigationLinks getBreadcrumbNavigationLinks(final String requestPath, final Localization localization) throws NavigationProviderException {
        return processNavigationLinks(requestPath, localization, new NavigationProcessing() {
            @Override
            public List<SitemapItem> processNavigation(SitemapItem navigationModel) {
                SitemapItem currentLevel = navigationModel.findWithUrl(stripDefaultExtension(requestPath));

                return currentLevel == null ? Collections.<SitemapItem>emptyList() : collectBreadcrumbsToLevel(currentLevel, localization);
            }

            @Override
            public NavigationLinks fallback(String requestPath, Localization localization) throws NavigationProviderException {
                return staticNavigationProvider.getBreadcrumbNavigationLinks(requestPath, localization);
            }
        });
    }

    protected abstract SitemapItem createTaxonomyNode(String taxonomyId, Localization localization);

    protected abstract String getNavigationTaxonomyId(Localization localization);

    String findIndexPageUrl(@NonNull List<SitemapItem> pageSitemapItems) {
        //noinspection StaticPseudoFunctionalStyleMethod
        SitemapItem index = Iterables.find(pageSitemapItems, new Predicate<SitemapItem>() {
            @Override
            public boolean apply(SitemapItem input) {
                return isIndexPath(input.getUrl());
            }
        }, null);

        log.trace("Index page is {} in {}", index, pageSitemapItems);
        return index != null ? stripIndexPath(index.getUrl()) : null;
    }

    SitemapItem createSitemapItemFromPage(PageMetaDTO page, String taxonomyId) {
        SitemapItem item = new SitemapItem();
        item.setId(getTaxonomySitemapIdentifier(taxonomyId, PAGE, String.valueOf(page.getId())));
        item.setType(sitemapItemTypePage);
        item.setTitle(page.getTitle());
        item.setUrl(stripDefaultExtension(page.getUrl()));
        item.setVisible(isVisibleItem(page.getTitle(), page.getUrl()));
        return item;
    }

    TaxonomyNode createTaxonomyNodeFromKeyword(@NotNull KeywordDTO keyword, String taxonomyId, String taxonomyNodeUrl, List<SitemapItem> children) {
        boolean isRoot = Objects.equals(keyword.getTaxonomyUri(), keyword.getKeywordUri());
        String keywordId = keyword.getKeywordUri().split("-")[1];

        TaxonomyNode node = new TaxonomyNode();
        node.setId(isRoot ? getTaxonomySitemapIdentifier(taxonomyId) : getTaxonomySitemapIdentifier(taxonomyId, KEYWORD, keywordId));
        node.setType(sitemapItemTypeTaxonomyNode);
        node.setUrl(stripDefaultExtension(taxonomyNodeUrl));
        node.setTitle(keyword.getName());
        node.setVisible(isVisibleItem(keyword.getName(), taxonomyNodeUrl));
        node.setItems(children);
        node.setKey(keyword.getKey());
        node.setWithChildren(keyword.isWithChildren() || keyword.getReferenceContentCount() > 0);
        node.setDescription(keyword.getDescription());
        node.setTaxonomyAbstract(keyword.isKeywordAbstract());
        node.setClassifiedItemsCount(keyword.getReferenceContentCount());
        return node;
    }

    private boolean isVisibleItem(String pageName, String pageUrl) {
        return LocalizationUtils.isWithSequenceDigits(pageName) && !isNullOrEmpty(pageUrl);
    }

    private NavigationLinks processNavigationLinks(String requestPath, Localization localization, NavigationProcessing navigationProcessing) throws NavigationProviderException {
        SitemapItem navigationModel = getNavigationModel(localization);
        if (isFallbackRequired(navigationModel, localization)) {
            return navigationProcessing.fallback(requestPath, localization);
        }

        return prepareItemsAsVisibleNavigation(localization, navigationProcessing.processNavigation(navigationModel));
    }

    @NotNull
    NavigationLinks prepareItemsAsVisibleNavigation(Localization localization, List<SitemapItem> navigationLinks) {
        Collection<SitemapItem> items = filterNavigationLinks(navigationLinks);

        return new NavigationLinks(resolveLinksUrl(localization, items));
    }

    @NotNull
    private List<Link> resolveLinksUrl(final Localization localization, Collection<SitemapItem> items) {
        return Lists.newArrayList(transform(items, new Function<SitemapItem, Link>() {
            @Override
            public Link apply(SitemapItem input) {
                return input.createLink(linkResolver, localization);
            }
        }));
    }

    @NotNull
    private Collection<SitemapItem> filterNavigationLinks(List<SitemapItem> navigationLinks) {
        return filter(navigationLinks, new Predicate<SitemapItem>() {
            @Override
            public boolean apply(SitemapItem input) {
                return input.isVisible() && !isNullOrEmpty(input.getUrl());
            }
        });
    }

    private boolean isFallbackRequired(String taxonomyId, Localization localization) {
        return logFallback(isNullOrEmpty(taxonomyId), localization);
    }

    private boolean isFallbackRequired(SitemapItem sitemapItem, Localization localization) {
        return logFallback(sitemapItem == null || !(sitemapItem instanceof TaxonomyNode), localization);
    }

    private boolean logFallback(boolean isFallback, Localization localization) {
        if (isFallback) {
            log.warn("Taxonomy navigation is not available, fallback to static navigation is required, localization {}", localization);
        }
        return isFallback;
    }

    private String getNavigationTaxonomyIdInternal(Localization localization) {
        String taxonomyId = getNavigationTaxonomyId(localization);
        log.trace("Taxonomy ID is {} for localization {}", taxonomyId, localization);
        return taxonomyId;
    }

    @NotNull
    private List<SitemapItem> collectBreadcrumbsToLevel(SitemapItem currentLevel, final Localization localization) {
        List<SitemapItem> breadcrumbs = new LinkedList<>();

        SitemapItem sitemapItem = currentLevel;

        boolean hasHome = false;
        while (sitemapItem.getParent() != null) {
            breadcrumbs.add(sitemapItem);
            hasHome = isHomePath(sitemapItem.getUrl(), localization);
            sitemapItem = sitemapItem.getParent();
        }

        // The Home TaxonomyNode/Keyword may be a top-level sibling instead of an ancestor
        if (!hasHome) {
            SitemapItem item = Iterables.find(sitemapItem.getItems(), new Predicate<SitemapItem>() {
                @Override
                public boolean apply(SitemapItem input) {
                    return isHomePath(input.getUrl(), localization);
                }
            }, null);
            if (item != null) {
                breadcrumbs.add(item);
            }
        }

        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    private interface NavigationProcessing {

        List<SitemapItem> processNavigation(SitemapItem navigationModel);

        NavigationLinks fallback(String requestPath, Localization localization) throws NavigationProviderException;
    }
}
