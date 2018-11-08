package com.sdl.dxa.tridion.navigation;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.DepthCounter;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.tridion.navigation.dynamic.NavigationModelProvider;
import com.sdl.dxa.tridion.navigation.dynamic.OnDemandNavigationModelProvider;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.generated.Ancestor;
import com.sdl.web.pca.client.contentmodel.generated.PageSitemapItem;
import com.sdl.web.pca.client.contentmodel.generated.SitemapItem;
import com.sdl.web.pca.client.contentmodel.generated.TaxonomySitemapItem;
import com.sdl.web.pca.client.exception.ApiClientException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sdl.dxa.tridion.common.ContextDataCreator.createContextData;
import static com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Sites;
import static com.sdl.web.pca.client.contentmodel.generated.Ancestor.NONE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Slf4j
@Service
@Profile("!cil.providers.active")
@Primary
public class GraphQLRestDynamicNavigationModelProvider implements NavigationModelProvider,
        OnDemandNavigationModelProvider {

    private final ApiClientProvider provider;

    private final int defaultDescendantDepth;

    @Autowired
    public GraphQLRestDynamicNavigationModelProvider(
            ApiClientProvider provider,
            @Value("${dxa.pca.dynamic.navigation.sitemap.descendant.depth:10}") int defaultDescendantDepth) {
        this.provider = provider;
        this.defaultDescendantDepth = defaultDescendantDepth;
    }

    @Override
    public Optional<TaxonomyNodeModelData> getNavigationModel(@NotNull SitemapRequestDto requestDto) {
        try {
            Optional<TaxonomySitemapItem> navigation = getNavigationModelInternal(requestDto);
            return navigation.map(item -> (TaxonomyNodeModelData) convert(item));
        } catch (ApiClientException e) {
            log.warn("Cannot find/load/convert dynamic navigation in the PCA for the request " + requestDto, e);
            return Optional.empty();
        }
    }

    private Optional<TaxonomySitemapItem> getNavigationModelInternal(@NotNull SitemapRequestDto request) {
        int depth = getDepth(request);
        ContextData contextData = createContextData(request.getClaims());

        TaxonomySitemapItem taxonomySitemapItem = provider.getClient()
                .getSitemap(Sites, request.getLocalizationId(), depth, contextData);
        if (taxonomySitemapItem == null) {
            return Optional.empty();
        }

        List<TaxonomySitemapItem> leafNodes = getLeafNodes(taxonomySitemapItem);
        while (leafNodes.size() > 0) {
            TaxonomySitemapItem node = leafNodes.remove(0);
            if (node.getHasChildNodes()) {
                TaxonomySitemapItem[] subtree = provider.getClient()
                        .getSitemapSubtree(Sites, request.getLocalizationId(), node.getId(), depth, NONE, contextData);
                if (node.getItems() == null) {
                    node.setItems(new ArrayList<>());
                }

                if (subtree.length > 0 && subtree[0].getItems() != null) {
                    node.getItems().addAll(subtree[0].getItems());
                }
                leafNodes.addAll(getLeafNodes(node));
            }
        }

        return Optional.of(taxonomySitemapItem);
    }

    @Override
    public Optional<Collection<SitemapItemModelData>> getNavigationSubtree(@NotNull SitemapRequestDto requestDto) {
        try {
            List<SitemapItem> navigation = getNavigationSubtreeInternal(requestDto);
            Collection<SitemapItemModelData> result = convert(navigation);

            // this needed because this interface method has a wrong result type
            // usually, empty collections means 'NO DATA'
            if (result.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(result);
            }
        } catch (ApiClientException e) {
            log.warn("Cannot find/load/convert dynamic subtree navigation in PCA for the request " + requestDto, e);
            return Optional.empty();
        }
    }

    private List<SitemapItem> getNavigationSubtreeInternal(@NotNull SitemapRequestDto requestDto) {
        int depth = getDepth(requestDto);
        ContextData contextData = createContextData(requestDto.getClaims());

        if (requestDto.getExpandLevels().isUnlimited()) {
            List<SitemapItem> entireTree = getEntireNavigationSubtreeInternal(requestDto);
            if (requestDto.getSitemapId() == null) {
                // if parent node not specified we return entire tree
                return entireTree;
            } else {
                // root node specified so return the direct children of this node
                return entireTree.stream()
                        .flatMap(sitemapItem -> ((TaxonomySitemapItem) sitemapItem).getItems().stream())
                        .sorted(Comparator.comparing(sitemapItem -> sitemapItem.getOriginalTitle()))
                        .collect(Collectors.toList());
            }
        } else {
            String sitemapId = requestDto.getSitemapId();
            boolean withAncestors = requestDto.getNavigationFilter().isWithAncestors();
            if (sitemapId == null) {
                // Requesting from root so just return descendants from root
                int adjustedDepth = depth > 0 ? depth - 1 : depth;
                return asList(provider.getClient().getSitemapSubtree(Sites, requestDto.getLocalizationId(),
                        null, adjustedDepth, withAncestors ? Ancestor.INCLUDE : NONE, contextData));
            } else if (withAncestors) {
                // we are looking for a particular item, we need to request the entire subtree first
                List<SitemapItem> entireTree = getEntireNavigationSubtreeInternal(requestDto);
                // Prune descendants from our deseried node
                SitemapItem node = findNode(entireTree, sitemapId);
                prune(node, 0, depth);
                return entireTree;
            } else {
                return asList(provider.getClient()
                        .getSitemapSubtree(Sites, requestDto.getLocalizationId(), sitemapId, depth, NONE, contextData))
                        .stream().map(item -> item.getItems())
                        .filter(sitemapItems -> sitemapItems != null)
                        .flatMap(sitemapItems -> sitemapItems.stream())
                        .sorted(Comparator.comparing(sitemapItem -> sitemapItem.getOriginalTitle()))
                        .collect(Collectors.toList());
            }
        }
    }

    private List<SitemapItem> getEntireNavigationSubtreeInternal(@NotNull SitemapRequestDto request) {
        int depth = getDepth(request);
        ContextData contextData = createContextData(request.getClaims());

        List<SitemapItem> rootItems = asList(provider.getClient()
                .getSitemapSubtree(Sites,
                        request.getLocalizationId(),
                        request.getSitemapId(),
                        depth,
                        request.getNavigationFilter().isWithAncestors() ? Ancestor.INCLUDE : NONE,
                        contextData));

        if (rootItems.isEmpty()) {
            return emptyList();
        }

        List<SitemapItem> tempRoots = new ArrayList<>(rootItems);

        int index = 0;
        while (index < tempRoots.size()) {
            TaxonomySitemapItem root = (TaxonomySitemapItem) tempRoots.get(index);
            List<TaxonomySitemapItem> leafNodes = getLeafNodes(root);
            for (TaxonomySitemapItem item : leafNodes) {
                TaxonomySitemapItem[] subtree = provider.getClient()
                        .getSitemapSubtree(Sites, request.getLocalizationId(), item.getId(), depth, NONE, contextData);
                if (subtree.length > 0) {
                    item.setItems(subtree[0].getItems());
                    tempRoots.addAll(getLeafNodes(item));
                }
            }
            index++;
        }
        return rootItems;
    }

    private List<TaxonomySitemapItem> getLeafNodes(SitemapItem rootNode) {
        List<TaxonomySitemapItem> leafNodes = new ArrayList<>();
        if (!(rootNode instanceof TaxonomySitemapItem)) {
            return leafNodes;
        }

        TaxonomySitemapItem root = (TaxonomySitemapItem) rootNode;
        if (!root.getHasChildNodes() || root.getItems() == null || root.getItems().isEmpty()) {
            return leafNodes;
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

    private SitemapItem findNode(List<SitemapItem> roots, String sitemapId) {
        for (SitemapItem node : roots) {
            if (node.getId().equals(sitemapId)) {
                return node;
            }

            if (node instanceof TaxonomySitemapItem) {
                TaxonomySitemapItem item = (TaxonomySitemapItem) node;
                if (item.getItems() != null && item.getItems().size() > 0) {
                    SitemapItem result = findNode(item.getItems(), sitemapId);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }

        return null;
    }

    private void prune(SitemapItem root, int currentLevel, int descendantLevels) {
        if (!(root instanceof TaxonomySitemapItem)) {
            return;
        }

        TaxonomySitemapItem item = (TaxonomySitemapItem) root;
        if (item.getItems() == null || item.getItems().size() == 0) {
            return;
        }

        if (currentLevel < descendantLevels) {
            for (SitemapItem next : item.getItems()) {
                prune(next, currentLevel + 1, descendantLevels);
            }
        } else {
            if (item.getItems() != null) {
                item.getItems().clear();
            }
        }
    }

    private int getDepth(@NotNull SitemapRequestDto requestDto) {
        int depth;
        DepthCounter expandLevels = requestDto.getExpandLevels();
        if (expandLevels.isUnlimited()) {
            depth = defaultDescendantDepth;
        } else {
            depth = expandLevels.getCounter();
        }
        return depth;
    }

    Collection<SitemapItemModelData> convert(List<SitemapItem> source) {
        return source.stream().map(sitemapItem -> convert(sitemapItem)).collect(Collectors.toList());
    }

    SitemapItemModelData convert(SitemapItem source) {
        SitemapItemModelData target;
        if (source instanceof TaxonomySitemapItem) {
            target = new TaxonomyNodeModelData();
        } else if (source instanceof PageSitemapItem) {
            target = new SitemapItemModelData();
        } else {
            throw new ApiClientException("Unsupported sitemap type " + source.getClass().getCanonicalName());
        }

        target.setId(source.getId());
        target.setOriginalTitle(source.getOriginalTitle());
        target.setTitle(source.getTitle());
        target.setType(source.getType());
        if (source.getPublishedDate() != null) {
            target.setPublishedDate(DateTime.parse(source.getPublishedDate()));
        }
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

}
