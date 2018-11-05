package com.sdl.dxa.tridion.navigation;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.tridion.navigation.dynamic.NavigationModelProvider;
import com.sdl.dxa.tridion.navigation.dynamic.OnDemandNavigationModelProvider;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.Ancestor;
import com.sdl.web.pca.client.contentmodel.generated.SitemapItem;
import com.sdl.web.pca.client.contentmodel.generated.TaxonomySitemapItem;
import com.sdl.web.pca.client.exception.ApiClientException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.sdl.dxa.tridion.common.ContextDataCreator.createContextData;

@Slf4j
//TODO fix by TSI-3494
//@Service
//@Profile("!cil.providers.active")
@Primary
public class GraphQLRestDynamicNavigationModelProvider implements NavigationModelProvider, OnDemandNavigationModelProvider {

    private final ApiClientProvider provider;

    @Autowired
    public GraphQLRestDynamicNavigationModelProvider(ApiClientProvider provider) {
        this.provider = provider;
    }

    @Override
    public Optional<TaxonomyNodeModelData> getNavigationModel(@NotNull SitemapRequestDto requestDto) {
        try {
            TaxonomySitemapItem taxonomySitemapItem = provider.getClient().getSitemap(ContentNamespace.Sites,
                    requestDto.getLocalizationId(),
                    requestDto.getExpandLevels().getCounter(),
                    createContextData(requestDto.getClaims()));
            TaxonomyNodeModelData converted = convert(taxonomySitemapItem);
            return Optional.of(converted);
        } catch (ApiClientException e) {
            log.warn("Cannot find/load/convert dynamic navigation in the Api Client for the request " + requestDto, e);
            return Optional.empty();
        }
    }

    TaxonomyNodeModelData convert(TaxonomySitemapItem source) {
        TaxonomyNodeModelData target = new TaxonomyNodeModelData();
        target.setTitle(source.getOriginalTitle());
        BeanUtils.copyProperties(source, target, "publishedDate", "items");
        target.setWithChildren(source.getHasChildNodes());
        target.setTaxonomyAbstract(source.getAbstract());
        target.setPublishedDate(DateTime.parse(source.getPublishedDate()));
        if (source.getItems() == null) return target;
        SortedSet<SitemapItemModelData> children = new TreeSet<>();
        for (SitemapItem child : source.getItems()) {
            if (child instanceof TaxonomySitemapItem) {
                children.add(convert((TaxonomySitemapItem)child));
            } else {
                throw new IllegalArgumentException("This copier takes only TaxonomySitemapItem as a child, but was " + child.getClass().getCanonicalName());
            }
        }
        target.setItems(children);
        return target;
    }

    @NotNull
    @Override
    public Optional<Collection<SitemapItemModelData>> getNavigationSubtree(@NotNull SitemapRequestDto requestDto) {
        try {
            TaxonomySitemapItem[] taxonomySitemapItem = provider.getClient().getSitemapSubtree(ContentNamespace.Sites,
                    requestDto.getLocalizationId(),
                    requestDto.getSitemapId(),
                    requestDto.getExpandLevels().getCounter(),
                    requestDto.getNavigationFilter().isWithAncestors() ? Ancestor.INCLUDE : Ancestor.NONE,
                    createContextData(requestDto.getClaims()));
            List<SitemapItemModelData> result = new ArrayList<>();
            if (taxonomySitemapItem != null) {
                for (TaxonomySitemapItem item : taxonomySitemapItem) {
                    result.add(convert(item));
                }
            }
            return Optional.of(result);
        } catch (ApiClientException e) {
            log.warn("Cannot find/load/convert dynamic subtree navigation in Api Client for the request " + requestDto, e);
            return Optional.empty();
        }
    }
}