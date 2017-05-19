package com.sdl.webapp.tridion.navigation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.web.api.taxonomies.TaxonomyRelationManager;
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
import com.sdl.webapp.common.api.navigation.TaxonomyUrisHolder;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.ItemTypes;
import com.tridion.broker.StorageException;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PageMetaFactory;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.taxonomies.filters.DepthFilter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static com.sdl.dxa.common.util.PathUtils.isHomePath;
import static com.sdl.dxa.common.util.PathUtils.isIndexPath;
import static com.sdl.dxa.common.util.PathUtils.isWithSequenceDigits;
import static com.sdl.dxa.common.util.PathUtils.stripDefaultExtension;
import static com.sdl.dxa.common.util.PathUtils.stripIndexPath;
import static com.sdl.webapp.common.api.navigation.TaxonomyUrisHolder.parse;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.KEYWORD;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.PAGE;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.getTaxonomySitemapIdentifier;
import static com.sdl.webapp.tridion.navigation.DynamicNavigationProvider.NavigationProcessing.FilterVisible.FILTERING;
import static com.sdl.webapp.tridion.navigation.DynamicNavigationProvider.NavigationProcessing.FilterVisible.NO_FILTERING;

/**
 * Navigation Provider implementation based on Taxonomies (Categories &amp; Keywords).
 * <p>Falls back to {@link AbstractStaticNavigationProvider} when dynamic navigation is not available.</p>
 */
@Slf4j
@Primary
@Service
@Profile("dynamic.navigation.provider")
public class DynamicNavigationProvider implements NavigationProvider, OnDemandNavigationProvider {

    private final AbstractStaticNavigationProvider staticNavigationProvider;

    private final LinkResolver linkResolver;

    private final TaxonomyFactory taxonomyFactory;

    private final TaxonomyRelationManager relationManager;

    @Value("${dxa.tridion.navigation.taxonomy.marker}")
    protected String taxonomyNavigationMarker;

    @Value("${dxa.tridion.navigation.taxonomy.type.taxonomyNode}")
    protected String sitemapItemTypeTaxonomyNode;

    @Value("${dxa.tridion.navigation.taxonomy.type.structureGroup}")
    protected String sitemapItemTypeStructureGroup;

    @Value("${dxa.tridion.navigation.taxonomy.type.page}")
    protected String sitemapItemTypePage;

    @Autowired
    public DynamicNavigationProvider(AbstractStaticNavigationProvider staticNavigationProvider,
                                     LinkResolver linkResolver,
                                     TaxonomyFactory taxonomyFactory,
                                     TaxonomyRelationManager relationManager) {
        this.staticNavigationProvider = staticNavigationProvider;
        this.linkResolver = linkResolver;
        this.taxonomyFactory = taxonomyFactory;
        this.relationManager = relationManager;
    }

    @Override
    @Cacheable(value = "default")
    public SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException {
        String taxonomyId = getNavigationTaxonomyIdInternal(localization);
        if (isFallbackRequired(taxonomyId, localization)) {
            return staticNavigationProvider.getNavigationModel(localization);
        }

        log.debug("Put navigation model for taxonomy id {} for localization id {} in cache", taxonomyId, localization.getId());
        return createTaxonomyNode(taxonomyId, localization);
    }

    @Override
    public NavigationLinks getTopNavigationLinks(String requestPath, final Localization localization) throws NavigationProviderException {
        return processNavigationLinks(requestPath, localization, new NavigationProcessing() {
            @Override
            public Collection<SitemapItem> processNavigation(SitemapItem navigationModel) {
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
            public Collection<SitemapItem> processNavigation(SitemapItem navigationModel) {
                SitemapItem currentLevel = navigationModel.findWithUrl(stripDefaultExtension(requestPath));

                if (currentLevel != null && !(currentLevel instanceof TaxonomyNode)) {
                    currentLevel = currentLevel.getParent();
                }

                return currentLevel == null ? Collections.emptyList() : currentLevel.getItems();
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
            public Collection<SitemapItem> processNavigation(SitemapItem navigationModel) {
                SitemapItem currentLevel = navigationModel.findWithUrl(stripDefaultExtension(requestPath));

                return currentLevel == null ? Collections.emptyList() : collectBreadcrumbsToLevel(currentLevel, localization);
            }

            @Override
            public NavigationLinks fallback(String requestPath, Localization localization) throws NavigationProviderException {
                return staticNavigationProvider.getBreadcrumbNavigationLinks(requestPath, localization);
            }
        }, NO_FILTERING);
    }

    @Override
    public Collection<SitemapItem> getNavigationSubtree(@Nullable String sitemapItemId, @NonNull NavigationFilter navigationFilter, @NonNull Localization localization) {
        log.trace("sitemapItemId: {}, Navigation filter: {}, localization {}", sitemapItemId, navigationFilter, localization);

        if (isNullOrEmpty(sitemapItemId)) {
            log.trace("Sitemap ID is empty, expanding taxonomy roots");
            return expandTaxonomyRoots(navigationFilter, localization);
        }

        TaxonomyUrisHolder info = parse(sitemapItemId, localization.getId());
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

    /**
     * Expands root Taxonomies.
     * We don't expect two equal Taxonomies at root level, so the method returns {@link Set}.
     *
     * @param navigationFilter navigation filter
     * @param localization     current localization
     * @return a set of root Taxonomies
     */
    @Contract("_, _ -> !null")
    protected Set<SitemapItem> expandTaxonomyRoots(final NavigationFilter navigationFilter, final Localization localization) {
        final int maximumDepth = navigationFilter.getDescendantLevels() > 0 ?
                navigationFilter.getDescendantLevels() - 1 : navigationFilter.getDescendantLevels();

        DepthFilter depthFilter = new DepthFilter(maximumDepth, DepthFilter.FILTER_DOWN);

        List<Keyword> roots = new ArrayList<>();
        String[] taxonomies = taxonomyFactory.getTaxonomies(TcmUtils.buildPublicationTcmUri(localization.getId()));
        for (String id : taxonomies) {
            roots.add(taxonomyFactory.getTaxonomyKeywords(id, depthFilter));
        }

        return new LinkedHashSet<>(transform(roots, new Function<Keyword, SitemapItem>() {
            @Override
            public SitemapItem apply(Keyword keyword) {
                return createTaxonomyNode(keyword, maximumDepth, navigationFilter, localization);
            }
        }));
    }

    /**
     * Expands descendants for a given {@link SitemapItem}.
     * We don't expect two equals items at the same level, so the method returns {@link Set}.
     *
     * @param uris             information about URI of current item
     * @param navigationFilter navigation filter
     * @param localization     curren localization
     * @return a set of descendants of item with passed URI
     */
    @Contract("_, _, _ -> !null")
    protected Set<SitemapItem> expandDescendants(TaxonomyUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
        if (uris.isPage()) {
            log.debug("Page cannot have descendants, return emptyList, uris = ", uris);
            return Collections.emptySet();
        }

        Keyword keyword = taxonomyFactory.getTaxonomyKeywords(uris.getTaxonomyUri(),
                new DepthFilter(navigationFilter.getDescendantLevels(), DepthFilter.FILTER_DOWN), uris.getKeywordUri());

        if (keyword == null) {
            log.warn("Keyword '{}' in Taxonomy '{}' was not found.", uris.getKeywordUri(), uris.getTaxonomyUri());
            return Collections.emptySet();
        }

        return createTaxonomyNode(keyword, navigationFilter.getDescendantLevels(), navigationFilter, localization).getItems();
    }

    @NotNull
    protected SitemapItem createTaxonomyNode(@NotNull String rootId, @NotNull Localization localization) {
        Keyword root = taxonomyFactory.getTaxonomyKeywords(rootId, new DepthFilter(DepthFilter.UNLIMITED_DEPTH, DepthFilter.FILTER_DOWN));

        return createTaxonomyNode(root, -1, NavigationFilter.DEFAULT, localization);
    }

    @Nullable
    protected String getNavigationTaxonomyId(Localization localization) {

        String[] taxonomies = taxonomyFactory.getTaxonomies(TcmUtils.buildPublicationTcmUri(localization.getId()));

        Keyword root = selectRootOfTaxonomy(taxonomies);
        if (root == null) {
            log.error("No Navigation Taxonomy Found in Localization [{}]. Ensure a Taxonomy with '{}}' in its title is published",
                    localization, taxonomyNavigationMarker);
            return null;
        }

        log.debug("Resolved Navigation Taxonomy: {}", root);

        return root.getTaxonomyURI();
    }

    private TaxonomyNode createTaxonomyNode(@NotNull Keyword keyword, int expandLevels, NavigationFilter filter, @NotNull Localization localization) {
        String taxonomyId = keyword.getTaxonomyURI().split("-")[1];

        String taxonomyNodeUrl = null;

        List<SitemapItem> children = new ArrayList<>();

        if (expandLevels != 0) {
            for (Keyword childKeyword : keyword.getKeywordChildren()) {
                children.add(createTaxonomyNode(childKeyword, expandLevels - 1, filter, localization));
            }

            if (keyword.getReferencedContentCount() > 0 && filter.getDescendantLevels() != 0) {
                List<SitemapItem> pageSitemapItems = getChildrenPages(keyword, taxonomyId, localization);

                taxonomyNodeUrl = findIndexPageUrl(pageSitemapItems);
                log.trace("taxonomyNodeUrl = {}", taxonomyNodeUrl);

                children.addAll(pageSitemapItems);
            }
        }

        for (SitemapItem child : children) {
            child.setTitle(PathUtils.removeSequenceFromPageTitle(child.getTitle()));
        }

        return createTaxonomyNodeFromKeyword(keyword, taxonomyId, taxonomyNodeUrl, new LinkedHashSet<>(children));
    }

    private List<SitemapItem> getChildrenPages(@NotNull Keyword keyword, @NotNull String taxonomyId, @NotNull Localization localization) {
        log.trace("Getting SitemapItems for all classified Pages (ordered by Page Title, including sequence prefix if any), " +
                "keyword {}, taxonomyId {}, localization {}", keyword, taxonomyId, localization);

        List<SitemapItem> items = new ArrayList<>();

        try {
            PageMetaFactory pageMetaFactory = new PageMetaFactory(Integer.parseInt(localization.getId()));
            PageMeta[] taxonomyPages = pageMetaFactory.getTaxonomyPages(keyword, false);
            for (PageMeta page : taxonomyPages) {
                items.add(createSitemapItemFromPage(page, taxonomyId));
            }
        } catch (StorageException e) {
            log.error("Error loading taxonomy pages for taxonomyId = {}, localizationId = {} and keyword {}", taxonomyId, localization.getId(), keyword, e);
        }

        return items;
    }

    String findIndexPageUrl(@NonNull List<SitemapItem> pageSitemapItems) {

        SitemapItem index = pageSitemapItems.stream()
                .filter(input -> isIndexPath(input.getUrl()))
                .findFirst()
                .orElse(null);

        log.trace("Index page is {} in {}", index, pageSitemapItems);
        return index != null ? stripIndexPath(index.getUrl()) : null;
    }

    SitemapItem createSitemapItemFromPage(PageMeta page, String taxonomyId) {
        SitemapItem item = new SitemapItem();
        item.setId(getTaxonomySitemapIdentifier(taxonomyId, PAGE, String.valueOf(page.getId())));
        item.setType(sitemapItemTypePage);
        item.setTitle(page.getTitle());
        item.setUrl(stripDefaultExtension(page.getURLPath()));
        item.setVisible(isVisibleItem(page.getTitle(), page.getURLPath()));
        item.setPublishedDate(new DateTime(page.getLastPublicationDate()));
        return item;
    }

    TaxonomyNode createTaxonomyNodeFromKeyword(@NotNull Keyword keyword, String taxonomyId, String taxonomyNodeUrl, Set<SitemapItem> children) {
        boolean isRoot = Objects.equals(keyword.getTaxonomyURI(), keyword.getKeywordURI());
        String keywordId = keyword.getKeywordURI().split("-")[1];

        TaxonomyNode node = new TaxonomyNode();
        node.setId(isRoot ? getTaxonomySitemapIdentifier(taxonomyId) : getTaxonomySitemapIdentifier(taxonomyId, KEYWORD, keywordId));
        node.setType(sitemapItemTypeTaxonomyNode);
        node.setUrl(stripDefaultExtension(taxonomyNodeUrl));
        node.setTitle(keyword.getKeywordName());
        node.setVisible(isVisibleItem(keyword.getKeywordName(), taxonomyNodeUrl));
        node.setItems(children);
        node.setKey(keyword.getKeywordKey());
        node.setWithChildren(keyword.hasKeywordChildren() || keyword.getReferencedContentCount() > 0);
        node.setDescription(keyword.getKeywordDescription());
        node.setTaxonomyAbstract(keyword.isKeywordAbstract());
        node.setClassifiedItemsCount(keyword.getReferencedContentCount());
        return node;
    }

    private boolean isVisibleItem(String pageName, String pageUrl) {
        return isWithSequenceDigits(pageName) && !isNullOrEmpty(pageUrl);
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
    NavigationLinks prepareItemsAsVisibleNavigation(Localization localization, Collection<SitemapItem> navigationLinks, boolean filter) {
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
    private Collection<SitemapItem> filterNavigationLinks(Collection<SitemapItem> navigationLinks) {
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
            hasHome = isHomePath(sitemapItem.getUrl(), localization.getPath());
            sitemapItem = sitemapItem.getParent();
        }

        // The Home TaxonomyNode/Keyword may be a top-level sibling instead of an ancestor
        if (!hasHome) {
            SitemapItem item = sitemapItem.getItems().stream()
                    .filter(input -> isHomePath(input.getUrl(), localization.getPath()))
                    .findFirst()
                    .orElse(null);
            if (item != null) {
                breadcrumbs.add(item);
            }
        }

        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    @NonNull
    private List<SitemapItem> expandAncestors(@NonNull TaxonomyUrisHolder uris, @NonNull NavigationFilter navigationFilter, @NonNull Localization localization) {
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
    protected TaxonomyNode expandAncestorsForKeyword(TaxonomyUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
        if (!uris.isKeyword()) {
            log.warn("Method for keywords was called for not a keyword! uris: {}, filter: {}, localization: {}", uris, navigationFilter, localization);
            return null;
        }

        DepthFilter depthFilter = new DepthFilter(DepthFilter.UNLIMITED_DEPTH, DepthFilter.FILTER_UP);
        Keyword taxonomyRoot = taxonomyFactory.getTaxonomyKeywords(uris.getTaxonomyUri(), depthFilter, uris.getKeywordUri());

        if (taxonomyRoot == null) {
            log.warn("Keyword {} in taxonomy {} wasn't found", uris.getKeywordUri(), uris.getTaxonomyUri());
            return null;
        }

        return createTaxonomyNode(taxonomyRoot, -1, navigationFilter, localization);
    }

    @Nullable
    private SitemapItem expandAncestorsForPage(TaxonomyUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
        List<SitemapItem> nodes = collectAncestorsForPage(uris, navigationFilter, localization);

        if (nodes.isEmpty()) {
            return null;
        }

        Iterator<SitemapItem> iterator = nodes.iterator();
        SitemapItem mergedNode = iterator.next();
        while (iterator.hasNext()) {
            mergeSubtrees(iterator.next(), mergedNode);
        }

        return mergedNode;
    }

    /**
     * Ancestors for a page is a list of same ROOT node with different children.
     * Basically, these different ROOTs (with same ID, because we are still within one taxonomy) contain
     * different children for different paths your page may be in.
     * <p>Unless other methods for descendants, this method returns {@link List} because the root Taxonomies will be the same object
     * even if page is in multiple places.</p>
     *
     * @param uris             URIs of your current context taxonomy node
     * @param navigationFilter navigation filter
     * @param localization     current localization
     * @return a list of roots of taxonomy with different paths for items
     */
    @Contract("_, _, _ -> !null")
    protected List<SitemapItem> collectAncestorsForPage(TaxonomyUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
        if (!uris.isPage()) {
            log.warn("Method for page was called for not a page! uris: {}, filter: {}, localization: {}", uris, navigationFilter, localization);
            return Collections.emptyList();
        }

        DepthFilter depthFilter = new DepthFilter(DepthFilter.UNLIMITED_DEPTH, DepthFilter.FILTER_UP);
        Keyword[] keywords = relationManager.getTaxonomyKeywords(uris.getTaxonomyUri(), uris.getPageUri(), null, depthFilter, ItemTypes.PAGE);

        if (keywords == null || keywords.length == 0) {
            log.debug("Page {} is not classified in taxonomy {}", uris.getPageUri(), uris.getTaxonomyUri());
            return Collections.emptyList();
        }

        List<SitemapItem> result = new ArrayList<>();
        for (Keyword keyword : keywords) {
            result.add(createTaxonomyNode(keyword, -1, navigationFilter, localization));
        }

        return result;
    }

    private void mergeSubtrees(@NonNull SitemapItem nodeToMerge, @NonNull SitemapItem mergedNode) {

        for (final SitemapItem childNode : nodeToMerge.getItems()) {
            SitemapItem childKeywordToMergeInto = mergedNode.getItems().stream()
                    .filter(input -> Objects.equals(childNode.getId(), input.getId()))
                    .findFirst().orElse(null);

            if (childKeywordToMergeInto == null) {
                mergedNode.addItem(childNode);
            } else {
                mergeSubtrees(childNode, childKeywordToMergeInto);
            }
        }
    }

    private Keyword selectRootOfTaxonomy(@NotNull String[] taxonomies) {
        for (String taxonomy : taxonomies) {
            Keyword keyword = taxonomyFactory.getTaxonomyKeyword(taxonomy);
            if (keyword.getKeywordName().contains(taxonomyNavigationMarker)) {
                return keyword;
            }
        }
        return null;
    }

    private void addDescendants(@NonNull SitemapItem taxonomyNode, NavigationFilter navigationFilter, Localization localization) {

        for (SitemapItem child : taxonomyNode.getItems()) {
            if (child instanceof TaxonomyNode) {
                addDescendants(child, navigationFilter, localization);
            }
        }

        Set<SitemapItem> additionalChildren = new LinkedHashSet<>(expandDescendants(parse(taxonomyNode.getId(), localization.getId()), navigationFilter, localization));

        for (SitemapItem child : difference(additionalChildren, newHashSet(taxonomyNode.getItems()))) {
            taxonomyNode.addItem(child);
        }
    }

    interface NavigationProcessing {

        Collection<SitemapItem> processNavigation(SitemapItem navigationModel);

        NavigationLinks fallback(String requestPath, Localization localization) throws NavigationProviderException;

        enum FilterVisible {
            FILTERING, NO_FILTERING
        }
    }
}
