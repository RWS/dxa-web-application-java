package com.sdl.dxa.tridion.navigation.dynamic;

import com.google.common.base.Strings;
import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.DepthCounter;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.dxa.exception.DxaTridionCommonException;
import com.sdl.web.api.dynamic.taxonomies.WebTaxonomyFactory;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.TaxonomyUrisHolder;
import com.sdl.webapp.common.controller.exception.BadRequestException;
import com.sdl.webapp.common.util.TcmUtils;
import com.tridion.ItemTypes;
import com.tridion.broker.StorageException;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PageMetaFactory;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyRelationManager;
import com.tridion.taxonomies.filters.DepthFilter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static com.sdl.dxa.common.util.PathUtils.isIndexPath;
import static com.sdl.dxa.common.util.PathUtils.isWithSequenceDigits;
import static com.sdl.dxa.common.util.PathUtils.removeSequenceFromPageTitle;
import static com.sdl.dxa.common.util.PathUtils.stripDefaultExtension;
import static com.sdl.webapp.common.api.navigation.TaxonomyUrisHolder.parse;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.KEYWORD;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.SitemapItemType.PAGE;
import static com.sdl.webapp.common.util.TcmUtils.Taxonomies.getTaxonomySitemapIdentifier;

@Slf4j
@Service
public class DynamicNavigationModelProviderImpl implements NavigationModelProvider, OnDemandNavigationModelProvider {

    private final WebTaxonomyFactory taxonomyFactory;

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
    public DynamicNavigationModelProviderImpl(WebTaxonomyFactory taxonomyFactory, TaxonomyRelationManager relationManager) {
        this.taxonomyFactory = taxonomyFactory;
        this.relationManager = relationManager;
    }

    @Override
    @Cacheable(value = "sitemaps", key = "{ #root.methodName, #requestDto }")
    public Optional<TaxonomyNodeModelData> getNavigationModel(@NotNull SitemapRequestDto requestDto) {
        List<Keyword> roots = getTaxonomyRoots(requestDto, keyword -> keyword.getKeywordName().contains(taxonomyNavigationMarker));
        if (roots.isEmpty()) {
            log.error("No Navigation Taxonomy Found in Localization [{}]. Ensure a Taxonomy with '{}' in its title is published",
                    requestDto.getLocalizationId(), taxonomyNavigationMarker);
            return Optional.empty();
        }

        Keyword rootTaxonomy = roots.get(0);
        log.debug("Resolved Navigation Taxonomy {} for request {}", rootTaxonomy, requestDto);

        return Optional.of(createTaxonomyNode(rootTaxonomy, requestDto));
    }

    @Override
    @NotNull
    @Cacheable(value = "sitemaps", key = "{ #root.methodName, #requestDto }")
    public Optional<Collection<SitemapItemModelData>> getNavigationSubtree(@NotNull SitemapRequestDto requestDto) {
        log.trace("Original sitemapRequestDto {}", requestDto);

        SitemapRequestDto request = requestDto.toBuilder()
                .expandLevels(new DepthCounter(requestDto.getNavigationFilter().getDescendantLevels()))
                .build();

        log.debug("Overridden depth counter using value from descendants level: {}", request);

        if (isNullOrEmpty(request.getSitemapId())) {
            if(request.getNavigationFilter().getDescendantLevels() != 0) {
                log.trace("Sitemap ID is empty, expanding all taxonomy roots");

                // normally expand level is equal to requested descendants level, but when we load categories (=roots) instead
                // top-level keywords, we add extra level and need to decrease expand level then by one
                SitemapRequestDto adaptedRequest = request.nextExpandLevel();

                return Optional.of(getTaxonomyRoots(adaptedRequest, keyword -> true).stream()
                        .map(keyword -> createTaxonomyNode(keyword, adaptedRequest))
                        .collect(Collectors.toList()));
            } else {
                return Optional.of(Collections.emptyList());
            }
        }

        TaxonomyUrisHolder info = parse(request.getSitemapId(), request.getLocalizationId());
        if (info == null) {
            throw new BadRequestException(String.format("SitemapID %s is wrong for Taxonomy navigation", request.getSitemapId()));
        }

        log.debug("Sitemap ID is known: {}", info);

        if (request.getNavigationFilter().isWithAncestors()) {
            log.trace("Filter with ancestors, expanding ancestors");
            Optional<SitemapItemModelData> taxonomy = taxonomyWithAncestors(info, request);
            return taxonomy.map(Collections::singletonList);
        }

        if (request.getNavigationFilter().getDescendantLevels() != 0 && !info.isPage()) {
            log.trace("Filter with descendants, expanding descendants");
            return expandDescendants(info, request);
        }

        log.trace("Filter is not specific, doing nothing");
        throw new BadRequestException(String.format("Request %s is not specific, doing nothing", request));
    }

    /**
     * Expands root Taxonomies.
     *
     * @param requestDto current request with mandatory localization ID and navigation filter
     * @param filter     way to filter roots, pass empty predicate always returning true {@code () -> true}
     * @return a list of root Taxonomies
     */
    @NotNull
    private List<Keyword> getTaxonomyRoots(@NotNull SitemapRequestDto requestDto, @NotNull() Predicate<Keyword> filter) {
        Assert.notNull(requestDto.getNavigationFilter(), "Navigation Filter is required to load taxonomy roots");

        NavigationFilter navigationFilter = requestDto.getNavigationFilter();

        // since we load categories here, we have to decrease depth by one because the first level is categories level
        // and we want top-level keywords
        final int maximumDepth = navigationFilter.getDescendantLevels() > 0 ?
                navigationFilter.getDescendantLevels() - 1 : navigationFilter.getDescendantLevels();
        DepthFilter depthFilter = new DepthFilter(maximumDepth, DepthFilter.FILTER_DOWN);

        return Arrays.stream(taxonomyFactory.getTaxonomies(TcmUtils.buildPublicationTcmUri(requestDto.getLocalizationId())))
                .distinct()
                .map(taxonomy -> taxonomyFactory.getTaxonomyKeywords(taxonomy, depthFilter))
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * Loads taxonomy with ancestors expanded for a given taxonomy URI and basing on a current request.
     * <p>For Keywords: one single ancestor for a given keyword. Although same keyword may be in few places, we don't expect it due to
     * technical limitation in CME. So basically we ignore the fact that keyword may be in many places (like page) and
     * expect only a single entry. Because of that we have only one taxonomy root for Keyword's ancestors.</p>
     * <p>For Pages: multiple ancestors are allowed for a page in case page is associated with multiple keywords.
     * Basically, these different ROOTs (with same ID, because we are still within one taxonomy) contain
     * different children for different paths your page may be in. Those subtrees are merged into one though, so we only have single node as a result.</p>
     *
     * @param uris       URIs of your current context taxonomy node
     * @param requestDto navigation filter
     * @return root of a taxonomy
     */
    @NonNull
    private Optional<SitemapItemModelData> taxonomyWithAncestors(@NonNull TaxonomyUrisHolder uris, @NotNull SitemapRequestDto requestDto) {
        if (uris.isTaxonomyOnly()) {
            String message = String.format("URIs %s is not a page nor keyword, can't expand ancestors, request %s", uris, requestDto);
            log.warn(message);
            throw new BadRequestException(message);
        }

        Optional<SitemapItemModelData> taxonomy = uris.isPage() ?
                expandAncestorsForPage(uris, requestDto) : expandAncestorsForKeyword(uris, requestDto);

        if (taxonomy.isPresent()) {
            if (requestDto.getNavigationFilter().getDescendantLevels() != 0) {
                addDescendantsToTaxonomy(taxonomy.get(), requestDto);
            }

            return taxonomy;
        }

        log.debug("Taxonomy was not found, uris {}, request {}", uris, requestDto);
        return Optional.empty();
    }

    private void addDescendantsToTaxonomy(@NonNull SitemapItemModelData taxonomy, @NotNull SitemapRequestDto requestDto) {
        taxonomy.getItems().stream()
                .filter(TaxonomyNodeModelData.class::isInstance)
                .forEach(child -> addDescendantsToTaxonomy(child, requestDto));

        TaxonomyUrisHolder uris = parse(taxonomy.getId(), requestDto.getLocalizationId());
        Set<SitemapItemModelData> children = new LinkedHashSet<>(expandDescendants(uris, requestDto).orElse(Collections.emptyList()));

        for (SitemapItemModelData child : difference(children, newHashSet(taxonomy.getItems()))) {
            taxonomy.addItem(child);
        }
    }

    /**
     * Loads taxonomy and expands descendants for a given taxonomy URI and basing on a current request.
     *
     * @param uris       information about URI of current item
     * @param requestDto current request data
     * @return an optional collection of descendants of item with passed URI
     */
    @NotNull
    private Optional<Collection<SitemapItemModelData>> expandDescendants(TaxonomyUrisHolder uris, @NotNull SitemapRequestDto requestDto) {
        if (uris.isPage()) {
            String message = "Page cannot have descendants, uris = " + uris;
            log.warn(message);
            throw new BadRequestException(message);
        }

        Keyword keyword = taxonomyFactory.getTaxonomyKeywords(uris.getTaxonomyUri(),
                new DepthFilter(requestDto.getNavigationFilter().getDescendantLevels(), DepthFilter.FILTER_DOWN), uris.getKeywordUri());

        if (keyword == null) {
            log.warn("Keyword '" + uris.getKeywordUri() + "' in Taxonomy '" + uris.getTaxonomyUri() + "' was not found.");
            return Optional.empty();
        }

        return Optional.of(createTaxonomyNode(keyword, requestDto).getItems());
    }

    @NotNull
    private Optional<SitemapItemModelData> expandAncestorsForPage(@NotNull TaxonomyUrisHolder uris, @NotNull SitemapRequestDto requestDto) {
        List<SitemapItemModelData> nodes = collectAncestorsForPage(uris, requestDto);

        if (nodes.isEmpty()) {
            return Optional.empty();
        }

        Iterator<SitemapItemModelData> iterator = nodes.iterator();
        SitemapItemModelData mergedNode = iterator.next();
        while (iterator.hasNext()) {
            mergeSubtrees(iterator.next(), mergedNode);
        }

        return Optional.of(mergedNode);
    }

    private void mergeSubtrees(@NonNull SitemapItemModelData sourceTree, @NonNull SitemapItemModelData targetTree) {
        for (final SitemapItemModelData sourceLeaf : sourceTree.getItems()) {
            Optional<SitemapItemModelData> targetLeaf = targetTree.getItems().stream()
                    .filter(input -> Objects.equals(sourceLeaf.getId(), input.getId()))
                    .findFirst();

            if (!targetLeaf.isPresent()) {
                targetTree.addItem(sourceLeaf);
            } else {
                mergeSubtrees(sourceLeaf, targetLeaf.get());
            }
        }
    }

    /**
     * Ancestors for a page is a list of same ROOT node with different children.
     * Basically, these different ROOTs (with same ID, because we are still within one taxonomy) contain
     * different children for different paths your page may be in.
     * <p>Unless other methods for descendants, this method returns {@link List} because the root Taxonomies will be the same object
     * even if page is in multiple places.</p>
     *
     * @param uris       URIs of your current context taxonomy node
     * @param requestDto current request data
     * @return a list of roots of taxonomy with different paths for items
     */
    @NotNull
    private List<SitemapItemModelData> collectAncestorsForPage(@NotNull TaxonomyUrisHolder uris, @NotNull SitemapRequestDto requestDto) {
        if (!uris.isPage()) {
            throw new IllegalArgumentException(String.format("Method for pages was called for not a page! uris: %s, request: %s", uris, requestDto));
        }

        DepthFilter depthFilter = new DepthFilter(DepthFilter.UNLIMITED_DEPTH, DepthFilter.FILTER_UP);
        Keyword[] keywords = relationManager.getTaxonomyKeywords(uris.getTaxonomyUri(), uris.getPageUri(), null, depthFilter, ItemTypes.PAGE);

        if (keywords == null || keywords.length == 0) {
            log.debug("Page {} is not classified in taxonomy {}", uris.getPageUri(), uris.getTaxonomyUri());
            return Collections.emptyList();
        }

        return Arrays.stream(keywords)
                .map(keyword -> createTaxonomyNode(keyword, requestDto.toBuilder().expandLevels(DepthCounter.UNLIMITED_DEPTH).build()))
                .collect(Collectors.toList());
    }

    /**
     * One single ancestor for a given keyword. Although same keyword may be in few places, we don't expect it due to
     * technical limitation in CME. So basically we ignore the fact that keyword may be in many places (like page) and
     * expect only a single entry. Because of that we have only one taxonomy root for Keyword's ancestors.
     *
     * @param uris       URIs of your current context taxonomy node
     * @param requestDto current request data
     * @return root of a taxonomy
     */
    @NotNull
    private Optional<SitemapItemModelData> expandAncestorsForKeyword(TaxonomyUrisHolder uris, SitemapRequestDto requestDto) {
        if (!uris.isKeyword()) {
            throw new IllegalArgumentException(String.format("Method for keywords was called for not a keyword! uris: %s, request: %s", uris, requestDto));
        }

        DepthFilter depthFilter = new DepthFilter(DepthFilter.UNLIMITED_DEPTH, DepthFilter.FILTER_UP);
        Keyword taxonomyRoot = taxonomyFactory.getTaxonomyKeywords(uris.getTaxonomyUri(), depthFilter, uris.getKeywordUri());

        if (taxonomyRoot == null) {
            log.warn("Keyword {} in taxonomy {} wasn't found", uris.getKeywordUri(), uris.getTaxonomyUri());
            return Optional.empty();
        }
        return Optional.of(createTaxonomyNode(taxonomyRoot, requestDto.toBuilder().expandLevels(DepthCounter.UNLIMITED_DEPTH).build()));
    }

    private TaxonomyNodeModelData createTaxonomyNode(@NotNull Keyword keyword, @NotNull SitemapRequestDto requestDto) {
        log.debug("Creating taxonomy node for keyword {} and request {}", keyword.getTaxonomyURI(), requestDto);
        String taxonomyId = String.valueOf(TcmUtils.getItemId(keyword.getTaxonomyURI()));

        List<SitemapItemModelData> children = new ArrayList<>();

        if (requestDto.getExpandLevels().isNotTooDeep()) {
            keyword.getKeywordChildren().forEach(child -> children.add(createTaxonomyNode(child, requestDto.nextExpandLevel())));
        }

        String taxonomyNodeUrl = getKeywordMetaUri(taxonomyId, requestDto, children, keyword, needsToAddChildren(keyword, requestDto));
        log.trace("taxonomyNodeUrl = {} found for taxonomyId = {}", taxonomyNodeUrl, taxonomyId);

        children.forEach(child -> child.setTitle(removeSequenceFromPageTitle(child.getTitle())));

        return createTaxonomyNodeFromKeyword(keyword, taxonomyId, taxonomyNodeUrl, new TreeSet<>(children));
    }

    private boolean needsToAddChildren(@NotNull Keyword keyword, @NotNull SitemapRequestDto requestDto) {
        return requestDto.getExpandLevels().isNotTooDeep() &&
            keyword.getReferencedContentCount() > 0 &&
            requestDto.getNavigationFilter().getDescendantLevels() != 0;
    }

    protected String getKeywordMetaUri(String taxonomyId, SitemapRequestDto requestDto, List<SitemapItemModelData> children, Keyword keyword, boolean needsToAddChildren) {
        if (keyword == null) return "";
        if (needsToAddChildren) {
            List<SitemapItemModelData> pageSitemapItems = getChildrenPages(keyword, taxonomyId, requestDto);
            children.addAll(pageSitemapItems);
            return findIndexPageUrl(pageSitemapItems).orElse(null);
        }
        return "";
    }

    private List<SitemapItemModelData> getChildrenPages(@NotNull Keyword keyword, @NotNull String taxonomyId, @NotNull SitemapRequestDto requestDto) {
        log.trace("Getting SitemapItems for all classified Pages (ordered by Page Title, including sequence prefix if any), " +
                "keyword {}, taxonomyId {}, localization {}", keyword, taxonomyId, requestDto.getLocalizationId());
        List<SitemapItemModelData> items = new ArrayList<>();
        try {
            PageMetaFactory pageMetaFactory = new PageMetaFactory(requestDto.getLocalizationId());
            PageMeta[] taxonomyPages = pageMetaFactory.getTaxonomyPages(keyword, false);
            return Arrays.stream(taxonomyPages)
                    .map(page -> createSitemapItemFromPage(page, taxonomyId))
                    .collect(Collectors.toList());
        } catch (StorageException e) {
            String message = "Error loading taxonomy pages for taxonomyId = " + taxonomyId + ", localizationId = " + requestDto.getLocalizationId() + " and keyword = " + keyword;
            throw new DxaTridionCommonException(message, e);
        }
    }

    private Optional<String> findIndexPageUrl(@NonNull List<SitemapItemModelData> pageSitemapItems) {
        return pageSitemapItems.stream()
                .filter(input -> isIndexPath(input.getUrl()))
                .findFirst()
                .map(SitemapItemModelData::getUrl)
                .map(PathUtils::stripIndexPath);
    }

    private SitemapItemModelData createSitemapItemFromPage(PageMeta page, String taxonomyId) {
        return new SitemapItemModelData()
                .setId(getTaxonomySitemapIdentifier(taxonomyId, PAGE, String.valueOf(page.getId())))
                .setType(sitemapItemTypePage)
                .setTitle(page.getTitle())
                .setUrl(stripDefaultExtension(page.getURLPath()))
                .setVisible(isVisibleItem(page.getTitle(), page.getURLPath()))
                .setPublishedDate(new DateTime(page.getLastPublicationDate()));
    }

    protected TaxonomyNodeModelData createTaxonomyNodeFromKeyword(@NotNull Keyword keyword, String taxonomyId, String taxonomyNodeUrl, SortedSet<SitemapItemModelData> children) {
        boolean isRoot = Objects.equals(keyword.getTaxonomyURI(), keyword.getKeywordURI());
        String keywordId = String.valueOf(TcmUtils.getItemId(keyword.getKeywordURI()));

        return (TaxonomyNodeModelData) new TaxonomyNodeModelData()
                .setWithChildren(keyword.hasKeywordChildren() || keyword.getReferencedContentCount() > 0)
                .setDescription(keyword.getKeywordDescription())
                .setTaxonomyAbstract(keyword.isKeywordAbstract())
                .setClassifiedItemsCount(keyword.getReferencedContentCount())
                .setKey(keyword.getKeywordKey())
                .setId(isRoot ? getTaxonomySitemapIdentifier(taxonomyId) : getTaxonomySitemapIdentifier(taxonomyId, KEYWORD, keywordId))
                .setType(sitemapItemTypeTaxonomyNode)
                .setUrl(complementUrlWithSlash(stripDefaultExtension(taxonomyNodeUrl)))
                .setTitle(keyword.getKeywordName())
                .setVisible(isVisibleItem(keyword.getKeywordName(), taxonomyNodeUrl))
                .setItems(children);
    }

    private String complementUrlWithSlash(String possibleUrl) {
        if (Strings.isNullOrEmpty(possibleUrl)) return "";
        if (!possibleUrl.startsWith("/")) return "/" + possibleUrl;
        return possibleUrl;
    }

    private boolean isVisibleItem(String pageName, String pageUrl) {
        return isWithSequenceDigits(pageName) && !isNullOrEmpty(pageUrl);
    }

}
