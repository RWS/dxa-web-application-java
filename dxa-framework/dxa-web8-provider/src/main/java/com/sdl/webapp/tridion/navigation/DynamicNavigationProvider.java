package com.sdl.webapp.tridion.navigation;


import com.google.common.base.Function;
import com.sdl.web.api.taxonomies.TaxonomyRelationManager;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.TaxonomySitemapItemUrisHolder;
import com.sdl.webapp.common.util.LocalizationUtils;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.navigation.data.KeywordDTO;
import com.sdl.webapp.tridion.navigation.data.PageMetaDTO;
import com.tridion.ItemTypes;
import com.tridion.broker.StorageException;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PageMetaFactory;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.taxonomies.filters.DepthFilter;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.providers.PayloadCacheProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;

@SuppressWarnings("Duplicates")
@Service
@Primary
@Slf4j
@Profile("dynamic.navigation.provider")
public class DynamicNavigationProvider extends AbstractDynamicNavigationProvider {

    private final TaxonomyFactory taxonomyFactory;

    private final TaxonomyRelationManager relationManager;

    @Autowired
    public DynamicNavigationProvider(StaticNavigationProvider staticNavigationProvider, LinkResolver linkResolver, TaxonomyFactory taxonomyFactory, PayloadCacheProvider cacheProvider, TaxonomyRelationManager relationManager) {
        super(staticNavigationProvider, linkResolver, cacheProvider);
        this.taxonomyFactory = taxonomyFactory;
        this.relationManager = relationManager;
    }

    @Override
    protected List<SitemapItem> expandTaxonomyRoots(final NavigationFilter navigationFilter, final Localization localization) {
        final int maximumDepth = navigationFilter.getDescendantLevels() > 0 ?
                navigationFilter.getDescendantLevels() - 1 : navigationFilter.getDescendantLevels();

        DepthFilter depthFilter = new DepthFilter(maximumDepth, DepthFilter.FILTER_DOWN);

        List<Keyword> roots = new ArrayList<>();
        String[] taxonomies = taxonomyFactory.getTaxonomies(TcmUtils.buildPublicationTcmUri(localization.getId()));
        for (String id : taxonomies) {
            roots.add(taxonomyFactory.getTaxonomyKeywords(id, depthFilter));
        }

        return newArrayList(transform(roots, new Function<Keyword, SitemapItem>() {
            @Override
            public SitemapItem apply(Keyword keyword) {
                return createTaxonomyNode(keyword, maximumDepth, navigationFilter, localization);
            }
        }));
    }

    @Override
    protected List<SitemapItem> expandDescendants(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
        if (uris.isPage()) {
            log.debug("Page cannot have descendants, return emptyList, uris = ", uris);
            return Collections.emptyList();
        }

        Keyword keyword = taxonomyFactory.getTaxonomyKeywords(uris.getTaxonomyUri(),
                new DepthFilter(navigationFilter.getDescendantLevels(), DepthFilter.FILTER_DOWN), uris.getKeywordUri());

        if (keyword == null) {
            log.warn("Keyword '{}' in Taxonomy '{}' was not found.", uris.getKeywordUri(), uris.getTaxonomyUri());
            return Collections.emptyList();
        }

        return createTaxonomyNode(keyword, navigationFilter.getDescendantLevels(), navigationFilter, localization).getItems();
    }

    @Override
    @NotNull
    protected SitemapItem createTaxonomyNode(@NotNull String rootId, @NotNull Localization localization) {
        Keyword root = taxonomyFactory.getTaxonomyKeywords(rootId, new DepthFilter(DepthFilter.UNLIMITED_DEPTH, DepthFilter.FILTER_DOWN));

        return createTaxonomyNode(root, localization);
    }

    @Override
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

    @Override
    protected TaxonomyNode expandAncestorsForKeyword(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
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

    @Override
    protected List<SitemapItem> collectAncestorsForPage(TaxonomySitemapItemUrisHolder uris, NavigationFilter navigationFilter, Localization localization) {
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

    private TaxonomyNode createTaxonomyNode(@NotNull Keyword keyword, @NotNull Localization localization) {
        return createTaxonomyNode(keyword, -1, NavigationFilter.DEFAULT, localization);
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
            child.setTitle(LocalizationUtils.removeSequenceFromPageTitle(child.getTitle()));
        }

        return createTaxonomyNodeFromKeyword(toDto(keyword), taxonomyId, taxonomyNodeUrl, new ArrayList<>(children));
    }

    private List<SitemapItem> getChildrenPages(@NotNull Keyword keyword, @NotNull String taxonomyId, @NotNull Localization localization) {
        log.trace("Getting SitemapItems for all classified Pages (ordered by Page Title, including sequence prefix if any), " +
                "keyword {}, taxonomyId {}, localization {}", keyword, taxonomyId, localization);

        List<SitemapItem> items = new ArrayList<>();

        try {
            PageMetaFactory pageMetaFactory = new PageMetaFactory(Integer.parseInt(localization.getId()));
            PageMeta[] taxonomyPages = pageMetaFactory.getTaxonomyPages(keyword, false);
            for (PageMeta page : taxonomyPages) {
                items.add(createSitemapItemFromPage(toDto(page), taxonomyId));
            }
        } catch (StorageException e) {
            log.error("Error loading taxonomy pages for taxonomyId = {}, localizationId = {} and keyword {}", taxonomyId, localization.getId(), keyword, e);
        }

        return items;
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

    private PageMetaDTO toDto(PageMeta pageMeta) {
        return PageMetaDTO.builder()
                .id(pageMeta.getId())
                .title(pageMeta.getTitle())
                .url(pageMeta.getURLPath())
                .publishedDate(pageMeta.getLastPublicationDate())
                .build();
    }

    private KeywordDTO toDto(Keyword keyword) {
        return KeywordDTO.builder()
                .keywordUri(keyword.getKeywordURI())
                .taxonomyUri(keyword.getTaxonomyURI())
                .name(keyword.getKeywordName())
                .key(keyword.getKeywordKey())
                .withChildren(keyword.hasKeywordChildren())
                .referenceContentCount(keyword.getReferencedContentCount())
                .description(keyword.getKeywordDescription())
                .keywordAbstract(keyword.isKeywordAbstract())
                .build();
    }
}
