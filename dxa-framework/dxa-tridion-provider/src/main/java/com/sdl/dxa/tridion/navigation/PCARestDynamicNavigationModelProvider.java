package com.sdl.dxa.tridion.navigation;

import com.google.common.base.Joiner;
import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.ClaimHolder;
import com.sdl.dxa.common.dto.DepthCounter;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.tridion.navigation.dynamic.NavigationModelProvider;
import com.sdl.dxa.tridion.navigation.dynamic.OnDemandNavigationModelProvider;
import com.sdl.dxa.tridion.pcaclient.PCAClientProvider;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.Ancestor;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValue;
import com.sdl.web.pca.client.contentmodel.generated.ClaimValueType;
import com.sdl.web.pca.client.contentmodel.generated.PageSitemapItem;
import com.sdl.web.pca.client.contentmodel.generated.SitemapItem;
import com.sdl.web.pca.client.contentmodel.generated.TaxonomySitemapItem;
import com.sdl.web.pca.client.exception.PublicContentApiException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Profile("!cil.providers.active")
@Primary
public class PCARestDynamicNavigationModelProvider implements NavigationModelProvider, OnDemandNavigationModelProvider {

    private final PCAClientProvider provider;

    private final int defaultDescendantDepth;

    @Autowired
    public PCARestDynamicNavigationModelProvider(
            PCAClientProvider provider,
            @Value("${dxa.pca.dynamic.navigation.sitemap.descendant.depth:10}") int defaultDescendantDepth) {
        this.provider = provider;
        this.defaultDescendantDepth = defaultDescendantDepth;
    }

    @Override
    public Optional<TaxonomyNodeModelData> getNavigationModel(@NotNull SitemapRequestDto requestDto) {
        try {
            Optional<TaxonomySitemapItem> navigation = getNavigationModelInternal(requestDto);
            return navigation.map(item -> (TaxonomyNodeModelData) convert(item));
        } catch (PublicContentApiException e) {
            log.warn("Cannot find/load/convert dynamic navigation in the PCA for the request " + requestDto, e);
            return Optional.empty();
        }
    }

    private Optional<TaxonomySitemapItem> getNavigationModelInternal(@NotNull SitemapRequestDto requestDto) {
        int depth;
        DepthCounter expandLevels = requestDto.getExpandLevels();
        if (expandLevels.isUnlimited()) {
            depth = defaultDescendantDepth;
        } else {
            depth = expandLevels.getCounter();
        }

        TaxonomySitemapItem taxonomySitemapItem = provider.getClient().getSitemap(
                ContentNamespace.Sites,
                requestDto.getLocalizationId(),
                depth,
                createContextData(requestDto.getClaims()));
        if (taxonomySitemapItem == null) {
            return Optional.empty();
        }

        List<TaxonomySitemapItem> leafNodes = getLeafNodes(taxonomySitemapItem);
        while (leafNodes.size() > 0) {
            TaxonomySitemapItem node = leafNodes.remove(0);
            if (node.getHasChildNodes()) {
                TaxonomySitemapItem[] subtree = provider.getClient().getSitemapSubtree(
                        ContentNamespace.Sites,
                        requestDto.getLocalizationId(),
                        node.getId(),
                        depth,
                        Ancestor.NONE,
                        createContextData(requestDto.getClaims()));
                if (node.getItems() == null) {
                    node.setItems(new ArrayList<>());
                }

                if (subtree != null && subtree.length > 0 && subtree[0].getItems() != null) {
                    node.getItems().addAll(subtree[0].getItems());
                }
                leafNodes.addAll(getLeafNodes(node));
            }
        }

        return Optional.of(taxonomySitemapItem);
    }

    private List<TaxonomySitemapItem> getLeafNodes(SitemapItem rootNode) {
        List<TaxonomySitemapItem> leafNodes = new ArrayList<>();
        if (!(rootNode instanceof TaxonomySitemapItem)) {
            return leafNodes;
        }

        TaxonomySitemapItem root = (TaxonomySitemapItem) rootNode;
        if (!root.getHasChildNodes()) {
            return leafNodes;
        }

        if (root.getItems() == null || root.getItems().isEmpty()) {
            return new ArrayList<>(Arrays.asList(root));
        }

        for (SitemapItem item : root.getItems()) {
            if (item instanceof TaxonomySitemapItem) {
                TaxonomySitemapItem node = (TaxonomySitemapItem) item;
                if (node.getHasChildNodes()) {
                    if (node.getItems() == null || node.getItems().isEmpty()) {
                        leafNodes.add(node);
                    } else {
                        leafNodes.addAll(getLeafNodes(node));
                    }
                }
            }
        }

        return leafNodes;
    }

    @NotNull
    ContextData createContextData(Map<String, ClaimHolder> claims) {
        ContextData contextData = new ContextData();
        if (claims.isEmpty()) {
            return contextData;
        }
        for (ClaimHolder holder : claims.values()) {
            contextData.addClaimValule(convertClaimHolderToClaimValue(holder));
        }
        return contextData;
    }

    ClaimValue convertClaimHolderToClaimValue(ClaimHolder holder) {
        ClaimValue claimValue = new ClaimValue();
        BeanUtils.copyProperties(holder, claimValue);
        String message = "ClaimValueType is not recognized, was used in " +
                holder + ", expected one of " + Joiner.on(";").join(ClaimValueType.values());
        if (holder.getClaimType() == null) throw new IllegalArgumentException(message);
        for (ClaimValueType type : ClaimValueType.values()) {
            if (holder.getClaimType().toUpperCase().equals(type.name())) {
                claimValue.setType(type);
            }
        }
        if (claimValue.getType() == null) throw new IllegalArgumentException(message);
        return claimValue;
    }

    SitemapItemModelData convert(SitemapItem source) {
        SitemapItemModelData target;
        if (source instanceof TaxonomySitemapItem) {
            target = new TaxonomyNodeModelData();
        } else if (source instanceof PageSitemapItem) {
            target = new SitemapItemModelData();
        } else {
            throw new PublicContentApiException("Unsupported sitemap type " + source.getClass().getCanonicalName());
        }

        target.setId(source.getId());
        target.setOriginalTitle(source.getOriginalTitle());
        target.setTitle(source.getTitle());
        target.setType(source.getType());
        target.setPublishedDate(DateTime.parse(source.getPublishedDate()));
        target.setUrl(source.getUrl());
        target.setVisible(source.getVisible());

        if (source instanceof TaxonomySitemapItem) {
            TaxonomyNodeModelData targetNode = (TaxonomyNodeModelData) target;
            TaxonomySitemapItem sourceNode = (TaxonomySitemapItem) source;

            targetNode.setKey(sourceNode.getKey());
            targetNode.setClassifiedItemsCount(sourceNode.getClassifiedItemsCount());
            targetNode.setDescription(sourceNode.getDescription());
            targetNode.setWithChildren(sourceNode.getHasChildNodes());
            targetNode.setTaxonomyAbstract(sourceNode.getAbstract());

            if (sourceNode.getItems() == null || sourceNode.getItems().isEmpty()) {
                return targetNode;
            }

            for (SitemapItem item : sourceNode.getItems()) {
                targetNode.getItems().add(convert(item));
            }
        }
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
        } catch (PublicContentApiException e) {
            log.warn("Cannot find/load/convert dynamic subtree navigation in PCA for the request " + requestDto, e);
            return Optional.empty();
        }
    }
}