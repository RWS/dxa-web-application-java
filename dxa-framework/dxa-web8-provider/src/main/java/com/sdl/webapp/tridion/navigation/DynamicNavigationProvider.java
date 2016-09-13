package com.sdl.webapp.tridion.navigation;


import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.tridion.navigation.data.KeywordDTO;
import com.sdl.webapp.tridion.navigation.data.PageMetaDTO;
import com.sdl.webapp.util.dd4t.TcmUtils;
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
import org.springframework.util.comparator.NullSafeComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("Duplicates")
@Service
@Primary
@Slf4j
@Profile("dynamic.navigation.provider")
public class DynamicNavigationProvider extends AbstractDynamicNavigationProvider {

    private static final NullSafeComparator<SitemapItem> SITEMAP_SORT_BY_TITLE = new NullSafeComparator<>(new Comparator<SitemapItem>() {
        @Override
        public int compare(SitemapItem o1, SitemapItem o2) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
    }, true);

    private final TaxonomyFactory taxonomyFactory;

    @Autowired
    public DynamicNavigationProvider(StaticNavigationProvider staticNavigationProvider, LinkResolver linkResolver, TaxonomyFactory taxonomyFactory, PayloadCacheProvider cacheProvider) {
        super(staticNavigationProvider, linkResolver, cacheProvider);
        this.taxonomyFactory = taxonomyFactory;
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

    private TaxonomyNode createTaxonomyNode(@NotNull Keyword keyword, @NotNull Localization localization) {
        String taxonomyId = keyword.getTaxonomyURI().split("-")[1];

        String taxonomyNodeUrl = null;

        Set<SitemapItem> children = new TreeSet<>(SITEMAP_SORT_BY_TITLE);

        for (Keyword childKeyword : keyword.getKeywordChildren()) {
            children.add(createTaxonomyNode(childKeyword, localization));
        }

        if (keyword.getReferencedContentCount() > 0) {
            List<SitemapItem> pageSitemapItems = addChildSitemapItemsForPages(keyword, taxonomyId, localization);

            taxonomyNodeUrl = findIndexPageUrl(pageSitemapItems);

            children.addAll(pageSitemapItems);
        }

        return createTaxonomyNodeFromKeyword(toDto(keyword), taxonomyId, taxonomyNodeUrl, new ArrayList<>(children));
    }

    private List<SitemapItem> addChildSitemapItemsForPages(@NotNull Keyword keyword, @NotNull String taxonomyId, @NotNull Localization localization) {
        Set<SitemapItem> items = new TreeSet<>(SITEMAP_SORT_BY_TITLE);

        try {
            PageMetaFactory pageMetaFactory = new PageMetaFactory(Integer.parseInt(localization.getId()));
            PageMeta[] taxonomyPages = pageMetaFactory.getTaxonomyPages(keyword, false);
            for (PageMeta page : taxonomyPages) {
                items.add(createSitemapItemFromPage(toDto(page), taxonomyId));
            }
        } catch (StorageException e) {
            log.error("Error loading taxonomy pages for taxonomyId = {}, localizationId = {} and keyword {}", taxonomyId, localization.getId(), keyword, e);
        }

        return new ArrayList<>(items);
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
