package com.sdl.webapp.tridion.navigation;


import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.util.dd4t.TcmUtils;
import com.tridion.broker.StorageException;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PageMetaFactory;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.taxonomies.filters.DepthFilter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.NullSafeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.sdl.webapp.util.dd4t.TcmUtils.Taxonomies.SitemapItemType.KEYWORD;
import static com.sdl.webapp.util.dd4t.TcmUtils.Taxonomies.SitemapItemType.PAGE;
import static com.sdl.webapp.util.dd4t.TcmUtils.Taxonomies.getTaxonomySitemapIdentifier;

@SuppressWarnings("Duplicates")
@Service
@Primary
@Slf4j
@Profile("dynamic.navigation.provider")
public class DynamicNavigationProvider extends AbstractDynamicNavigationProvider {

    private final TaxonomyFactory taxonomyFactory;

    @Autowired
    public DynamicNavigationProvider(StaticNavigationProvider staticNavigationProvider, LinkResolver linkResolver, TaxonomyFactory taxonomyFactory) {
        super(staticNavigationProvider, linkResolver);
        this.taxonomyFactory = taxonomyFactory;
    }

    @Override
    protected List<SitemapItem> getTopNavigationLinksInternal(String taxonomyId, Localization localization) {
        Keyword taxonomyRoot = taxonomyFactory.getTaxonomyKeywords(taxonomyId, new DepthFilter(1, DepthFilter.FILTER_DOWN));
        return createTaxonomyNode(taxonomyRoot, localization).getItems();
    }

    @Override
    @NotNull
    protected SitemapItem createTaxonomyNode(@NotNull String rootId, @NotNull Localization localization) {
        Keyword root = taxonomyFactory.getTaxonomyKeywords(rootId, new DepthFilter(-1, DepthFilter.FILTER_DOWN));
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

        List<SitemapItem> children = new ArrayList<>();

        for (Keyword childKeyword : keyword.getKeywordChildren()) {
            children.add(createTaxonomyNode(childKeyword, localization));
        }

        if (keyword.getReferencedContentCount() > 0) {
            List<SitemapItem> pageSitemapItems = addChildSitemapItemsForPages(keyword, taxonomyId, localization);

            taxonomyNodeUrl = findIndexPageUrl(pageSitemapItems);

            children.addAll(pageSitemapItems);
        }

        return createTaxonomyNode(keyword, taxonomyId, taxonomyNodeUrl, children);
    }

    private TaxonomyNode createTaxonomyNode(@NotNull Keyword keyword, String taxonomyId, String taxonomyNodeUrl, List<SitemapItem> children) {
        boolean isRoot = Objects.equals(keyword.getTaxonomyURI(), keyword.getKeywordURI());
        String keywordId = keyword.getKeywordURI().split("-")[1];

        TaxonomyNode node = new TaxonomyNode();
        node.setId(isRoot ? getTaxonomySitemapIdentifier(taxonomyId) : getTaxonomySitemapIdentifier(taxonomyId, KEYWORD, keywordId));
        node.setType(sitemapItemTypeTaxonomyNode);
        node.setUrl(taxonomyNodeUrl);
        node.setTitle(keyword.getKeywordName());
        node.setVisible(true);
        node.setItems(children);
        node.setKey(keyword.getKeywordKey());
        node.setWithChildren(keyword.hasKeywordChildren() || keyword.getReferencedContentCount() > 0);
        node.setDescription(keyword.getKeywordDescription());
        node.setTaxonomyAbstract(keyword.isKeywordAbstract());
        node.setClassifiedItemsCount(keyword.getReferencedContentCount());
        return node;
    }

    private List<SitemapItem> addChildSitemapItemsForPages(@NotNull Keyword keyword, @NotNull String taxonomyId, @NotNull Localization localization) {
        List<SitemapItem> items = new ArrayList<>();

        try {
            PageMetaFactory pageMetaFactory = new PageMetaFactory(localization.getId());
            PageMeta[] taxonomyPages = pageMetaFactory.getTaxonomyPages(keyword, false);
            for (PageMeta page : taxonomyPages) {
                SitemapItem sitemapItem = createSitemapItem(page, taxonomyId);
                items.add(sitemapItem);
            }
        } catch (StorageException e) {
            log.error("Error loading taxonomy pages for taxonomyId = {}, localizationId = {} and keyword {}", taxonomyId, localization.getId(), keyword, e);
        }

        Collections.sort(items, new NullSafeComparator<>(new Comparator<SitemapItem>() {
            @Override
            public int compare(SitemapItem o1, SitemapItem o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        }, true));

        return items;
    }

    private SitemapItem createSitemapItem(PageMeta page, String taxonomyId) {
        SitemapItem item = new SitemapItem();
        item.setId(getTaxonomySitemapIdentifier(taxonomyId, PAGE, String.valueOf(page.getId())));
        item.setType(sitemapItemTypePage);
        item.setTitle(page.getTitle());
        item.setUrl(page.getURLPath());
        item.setVisible(true);
        return item;
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
}
