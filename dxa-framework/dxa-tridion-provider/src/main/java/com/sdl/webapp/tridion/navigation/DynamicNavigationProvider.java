package com.sdl.webapp.tridion.navigation;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.DepthCounter;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.dxa.tridion.navigation.dynamic.NavigationModelProvider;
import com.sdl.dxa.tridion.navigation.dynamic.OnDemandNavigationModelProvider;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.entity.TaxonomyNode;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.api.navigation.OnDemandNavigationProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.sdl.dxa.common.util.PathUtils.isHomePath;

/**
 * Navigation Provider implementation based on Taxonomies (Categories &amp; Keywords).
 * <p>Falls back to {@link StaticNavigationProvider} when dynamic navigation is not available.</p>
 */
@Slf4j
@Primary
@Service
@Profile("dynamic.navigation.provider")
public class DynamicNavigationProvider implements NavigationProvider, OnDemandNavigationProvider {

    private final StaticNavigationProvider staticNavigationProvider;

    private final NavigationModelProvider navigationModelProvider;

    private final OnDemandNavigationModelProvider onDemandNavigationModelProvider;

    private final LinkResolver linkResolver;

    @Autowired
    public DynamicNavigationProvider(StaticNavigationProvider staticNavigationProvider,
                                     LinkResolver linkResolver,
                                     NavigationModelProvider navigationModelProvider,
                                     OnDemandNavigationModelProvider onDemandNavigationModelProvider) {
        this.staticNavigationProvider = staticNavigationProvider;
        this.linkResolver = linkResolver;
        this.navigationModelProvider = navigationModelProvider;
        this.onDemandNavigationModelProvider = onDemandNavigationModelProvider;
    }

    @Override
    public SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException {
        Optional<SitemapItemModelData> navigationModel = _getNavigationModel(localization);

        if (!navigationModel.isPresent()) {
            return staticNavigationProvider.getNavigationModel(localization);
        }

        return _convert(navigationModel.get());
    }

    @Override
    public NavigationLinks getTopNavigationLinks(String requestPath, Localization localization) throws NavigationProviderException {
        Optional<SitemapItemModelData> navigationModel = _getNavigationModel(localization);

        if (!navigationModel.isPresent()) {
            return staticNavigationProvider.getTopNavigationLinks(requestPath, localization);
        }

        return _toNavigationLinks(navigationModel.get().getItems(), true, localization);
    }

    @Override
    public NavigationLinks getContextNavigationLinks(String requestPath, Localization localization) throws NavigationProviderException {
        Optional<SitemapItemModelData> navigationModel = _getNavigationModel(localization);

        if (!navigationModel.isPresent()) {
            return staticNavigationProvider.getContextNavigationLinks(requestPath, localization);
        }

        SitemapItemModelData currentLevel = navigationModel.get().findWithUrl(PathUtils.stripDefaultExtension(requestPath));

        if (currentLevel != null && !(currentLevel instanceof TaxonomyNodeModelData)) {
            currentLevel = currentLevel.getParent();
        }

        return _toNavigationLinks(currentLevel == null ? Collections.emptySet() : currentLevel.getItems(), true, localization);
    }

    @Override
    public NavigationLinks getBreadcrumbNavigationLinks(String requestPath, Localization localization) throws NavigationProviderException {
        Optional<SitemapItemModelData> navigationModel = _getNavigationModel(localization);

        if (!navigationModel.isPresent()) {
            return staticNavigationProvider.getBreadcrumbNavigationLinks(requestPath, localization);
        }

        SitemapItemModelData currentLevel = navigationModel.get().findWithUrl(PathUtils.stripDefaultExtension(requestPath));

        return _toNavigationLinks(currentLevel == null ? Collections.emptySet() : _collectBreadcrumbsToLevel(currentLevel, localization),
                false, localization);
    }

    @Override
    public Collection<SitemapItem> getNavigationSubtree(@Nullable String sitemapItemId, @NonNull NavigationFilter navigationFilter, @NonNull Localization localization) {
        Optional<Collection<SitemapItemModelData>> subtree;
        SitemapRequestDto requestDto = SitemapRequestDto
                .builder(Integer.parseInt(localization.getId()))
                .navigationFilter(navigationFilter)
                .expandLevels(new DepthCounter(navigationFilter.getDescendantLevels()))
                .sitemapId(sitemapItemId)
                .build();

        subtree = onDemandNavigationModelProvider.getNavigationSubtree(requestDto);

        if (!subtree.isPresent()) {
            log.debug("Nothing found for the given request {}", requestDto);
            return Collections.emptyList();
        }

        return subtree.get().stream()
                .map(this::_convert)
                .collect(Collectors.toList());
    }

    @NotNull
    private NavigationLinks _toNavigationLinks(Collection<SitemapItemModelData> items,
                                               boolean onlyVisible, Localization localization) {
        return items.stream()
                .filter(model -> !onlyVisible || (model.isVisible() && !isNullOrEmpty(model.getUrl())))
                .map(this::_convert)
                .map(item -> item.createLink(linkResolver, localization))
                .collect(Collectors.collectingAndThen(Collectors.toList(), NavigationLinks::new));
    }

    @NotNull
    private List<SitemapItemModelData> _collectBreadcrumbsToLevel(SitemapItemModelData currentLevel,
                                                                  final Localization localization) {
        List<SitemapItemModelData> breadcrumbs = new LinkedList<>();

        SitemapItemModelData model = currentLevel;

        boolean hasHome = false;
        while (model.getParent() != null) {
            breadcrumbs.add(model);
            hasHome = isHomePath(model.getUrl(), localization.getPath());
            model = model.getParent();
        }

        // The Home TaxonomyNode/Keyword may be a top-level sibling instead of an ancestor
        if (!hasHome) {
            SitemapItemModelData item = model.getItems().stream()
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

    @NotNull
    private Optional<SitemapItemModelData> _getNavigationModel(Localization localization) {
        SitemapRequestDto requestDto = SitemapRequestDto.wholeTree(Integer.parseInt(localization.getId())).build();
        Optional<TaxonomyNodeModelData> navigationModel = navigationModelProvider.getNavigationModel(requestDto);
        if (!navigationModel.isPresent()) {
            log.warn("Taxonomy navigation is not available, fallback to static navigation is required, localizationId {}", localization.getId());
            return Optional.empty();
        }
        Assert.isInstanceOf(TaxonomyNodeModelData.class, navigationModel.get(), "Navigation model should always be a taxonomy node");
        return Optional.of(navigationModel.get());
    }

    @NotNull
    private SitemapItem _convert(@NotNull SitemapItemModelData model) {
        SitemapItem item = _instantiateSitemap(model);
        item.setId(model.getId());
        item.setVisible(model.isVisible());
        item.setUrl(model.getUrl());
        item.setTitle(model.getTitle());
        item.setOriginalTitle(model.getOriginalTitle());
        item.setPublishedDate(model.getPublishedDate());
        item.setType(model.getType());
        model.getItems().forEach(modelData -> item.addItem(_convert(modelData)));
        return item;
    }

    @NotNull
    private SitemapItem _instantiateSitemap(@NotNull SitemapItemModelData model) {
        if (model instanceof TaxonomyNodeModelData) {
            TaxonomyNodeModelData taxonomyModel = (TaxonomyNodeModelData) model;
            TaxonomyNode item = new TaxonomyNode();
            item.setKey(taxonomyModel.getKey());
            item.setClassifiedItemsCount(taxonomyModel.getClassifiedItemsCount());
            item.setDescription(taxonomyModel.getDescription());
            item.setTaxonomyAbstract(taxonomyModel.isTaxonomyAbstract());
            item.setWithChildren(taxonomyModel.isWithChildren());
            return item;
        } else {
            return new SitemapItem();
        }
    }
}
