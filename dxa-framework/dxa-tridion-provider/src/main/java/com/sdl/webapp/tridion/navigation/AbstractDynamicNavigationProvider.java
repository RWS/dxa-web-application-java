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
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.api.navigation.OnDemandNavigationProvider;
import com.sdl.webapp.common.api.navigation.TaxonomySitemapItemUrisHolder;
import com.sdl.webapp.common.util.LocalizationUtils;
import com.sdl.webapp.tridion.navigation.data.KeywordDTO;
import com.sdl.webapp.tridion.navigation.data.PageMetaDTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.providers.PayloadCacheProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static com.sdl.webapp.common.api.navigation.TaxonomySitemapItemUrisHolder.parse;
import static com.sdl.webapp.common.util.LocalizationUtils.isHomePath;
import static com.sdl.webapp.common.util.LocalizationUtils.isIndexPath;
import static com.sdl.webapp.common.util.LocalizationUtils.stripDefaultExtension;
import static com.sdl.webapp.common.util.LocalizationUtils.stripIndexPath;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.KEYWORD;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.PAGE;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.getTaxonomySitemapIdentifier;
import static com.sdl.webapp.tridion.navigation.AbstractDynamicNavigationProvider.NavigationProcessing.FilterVisible.FILTERING;
import static com.sdl.webapp.tridion.navigation.AbstractDynamicNavigationProvider.NavigationProcessing.FilterVisible.NO_FILTERING;

/**
 * Navigation Provider implementation based on Taxonomies (Categories &amp; Keywords).
 * <p>Falls back to {@link StaticNavigationProvider} when dynamic navigation is not available.</p>
 */
@Slf4j
public abstract class AbstractDynamicNavigationProvider implements NavigationProvider, OnDemandNavigationProvider {

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
        }, FILTERING);
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

                return currentLevel == null ? Collections.<SitemapItem>emptyList() : currentLevel.getItems();
            }

            @Override
            public NavigationLinks fallback(String requestPath, Localization localization) throws NavigationProviderException {
                return staticNavigationProvider.getContextNavigationLinks(requestPath, localization);
            }
        }, FILTERING);
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
        }, NO_FILTERING);
    }

    @Override
    public List<SitemapItem> getNavigationSubtree(@Nullable String sitemapItemId, @NonNull NavigationFilter navigationFilter, @NonNull Localization localization) {
        log.trace("sitemapItemId: {}, Navigation filter: {}, localization {}", sitemapItemId, navigationFilter, localization);

        if (isNullOrEmpty(sitemapItemId)) {
            log.trace("Sitemap ID is empty, expanding taxonomy roots");
            return expandTaxonomyRoots(navigationFilter, localization);
        }

        TaxonomySitemapItemUrisHolder info = parse(sitemapItemId, localization);
        if (info == null) {
            log.warn("SitemapID {} is wrong for Taxonomy navigation, return empty list of items", sitemapItemId);
            return Collections.emptyList();
        }

        if (navigationFilter.isWithAncestors()) {
            log.trace("Filter with ancestors, expanding ancestors");
            return expandAncestors(info, navigationFilter, localization);
        }

        if (navigationFilter.getDescendantLevels() != 0 && !info.isPage()) {
            log.trace("Filter with descendants, expanding descendants");
            return expandDescendants(info, navigationFilter, localization);
        }

        log.trace("Filter is not specific, doing nothing");
        return Collections.emptyList();
    }

    @Contract("_, _ -> !null")
    protected abstract List<SitemapItem> expandTaxonomyRoots(NavigationFilter navigationFilter, Localization localization);

    @Contract("_, _, _ -> !null")
    protected abstract List<SitemapItem> expandDescendants(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization);

    protected abstract SitemapItem createTaxonomyNode(String taxonomyId, Localization localization);

    protected abstract String getNavigationTaxonomyId(Localization localization);

    /**
     * One single ancestor for a given keyword. Although same keyword may be in few places, we don't expect it due to
     * technical limitation in CME. So basically we ignore the fact that keyword may be in many places (like page) and
     * expect only a single entry. Because of that we have only one taxonomy root for Keyword's ancestors.
     *
     * @param uris             URIs of your current context taxonomy node
     * @param navigationFilter navigation filter
     * @param localization     current localization
     * @return root of a taxonomy
     */
    @Nullable
    protected abstract TaxonomyNode expandAncestorsForKeyword(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization);

    /**
     * Ancestors for a page is a list of same ROOT node with different children.
     * Basically, these different ROOTs (with same ID, because we are still within one taxonomy) contain
     * different children for different paths your page may be in.
     *
     * @param uris             URIs of your current context taxonomy node
     * @param navigationFilter navigation filter
     * @param localization     current localization
     * @return a list of roots of taxonomy with different paths for items
     */
    @Contract("_, _, _ -> !null")
    protected abstract List<SitemapItem> collectAncestorsForPage(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization);

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

    private NavigationLinks processNavigationLinks(String requestPath, Localization localization,
                                                   NavigationProcessing navigationProcessing, NavigationProcessing.FilterVisible filter) throws NavigationProviderException {
        SitemapItem navigationModel = getNavigationModel(localization);
        if (isFallbackRequired(navigationModel, localization)) {
            return navigationProcessing.fallback(requestPath, localization);
        }

        return prepareItemsAsVisibleNavigation(localization,
                navigationProcessing.processNavigation(navigationModel), filter == FILTERING);
    }

    @NotNull
    NavigationLinks prepareItemsAsVisibleNavigation(Localization localization, List<SitemapItem> navigationLinks, boolean filter) {
        Collection<SitemapItem> items = filter ? filterNavigationLinks(navigationLinks) : navigationLinks;

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
            log.warn("Taxonomy navigation is not available, fallback to static navigation is required, localizationId {}", localization.getId());
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

    @NonNull
    private List<SitemapItem> expandAncestors(@NonNull TaxonomySitemapItemUrisHolder uris, @NonNull NavigationFilter navigationFilter, @NonNull Localization localization) {
        if (!uris.isPage() && !uris.isKeyword()) {
            log.debug("URIs {} is not a page nor keyword, can't expand ancestors, filter {}, localization {}", uris, navigationFilter, localization);
            return Collections.emptyList();
        }

        SitemapItem taxonomyRoot = uris.isPage() ?
                expandAncestorsForPage(uris, navigationFilter, localization) :
                expandAncestorsForKeyword(uris, navigationFilter, localization);

        if (taxonomyRoot != null) {
            if (navigationFilter.getDescendantLevels() != 0) {
                addDescendants(taxonomyRoot, navigationFilter, localization);
            }

            return Lists.newArrayList(taxonomyRoot);
        }

        log.debug("Taxonomy root is null, can't find it, returning empty list, uris {}, localization {}, filter {}", uris, localization, navigationFilter);
        return Collections.emptyList();
    }

    @Nullable
    private SitemapItem expandAncestorsForPage(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
        List<SitemapItem> nodes = collectAncestorsForPage(uris, navigationFilter, localization);

        if (nodes.isEmpty()) {
            return null;
        }

        SitemapItem mergedNode = nodes.get(0);
        ListIterator<SitemapItem> iterator = nodes.listIterator(1);
        while (iterator.hasNext()) {
            mergeSubtrees(iterator.next(), mergedNode);
        }

        return mergedNode;
    }

    private void mergeSubtrees(@NonNull SitemapItem nodeToMerge, @NonNull SitemapItem mergedNode) {

        for (final SitemapItem childNode : nodeToMerge.getItems()) {
            SitemapItem childKeywordToMergeInto = Iterables.find(mergedNode.getItems(), new Predicate<SitemapItem>() {
                @Override
                public boolean apply(SitemapItem input) {
                    return Objects.equals(childNode.getId(), input.getId());
                }
            }, null);
            if (childKeywordToMergeInto == null) {
                mergedNode.addItem(childNode);
            } else {
                mergeSubtrees(childNode, childKeywordToMergeInto);
            }
        }
    }

    private void addDescendants(@NonNull SitemapItem taxonomyNode, NavigationFilter navigationFilter, Localization localization) {

        for (SitemapItem child : taxonomyNode.getItems()) {
            if (child instanceof TaxonomyNode) {
                addDescendants(child, navigationFilter, localization);
            }
        }

        Set<SitemapItem> additionalChildren = new LinkedHashSet<>(expandDescendants(parse(taxonomyNode.getId(), localization), navigationFilter, localization));

        for (SitemapItem child : difference(additionalChildren, newHashSet(taxonomyNode.getItems()))) {
            taxonomyNode.addItem(child);
        }
    }

    interface NavigationProcessing {

        List<SitemapItem> processNavigation(SitemapItem navigationModel);

        NavigationLinks fallback(String requestPath, Localization localization) throws NavigationProviderException;

        enum FilterVisible {
            FILTERING, NO_FILTERING
        }
    }
}
