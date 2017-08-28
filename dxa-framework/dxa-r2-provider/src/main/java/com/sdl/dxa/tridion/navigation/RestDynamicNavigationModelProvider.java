package com.sdl.dxa.tridion.navigation;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.api.datamodel.model.TaxonomyNodeModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceConfiguration;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.dxa.tridion.navigation.dynamic.NavigationModelProvider;
import com.sdl.dxa.tridion.navigation.dynamic.OnDemandNavigationModelProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Primary
public class RestDynamicNavigationModelProvider implements NavigationModelProvider, OnDemandNavigationModelProvider {

    private final ModelServiceConfiguration configuration;

    private final ModelServiceClient modelServiceClient;

    @Autowired
    public RestDynamicNavigationModelProvider(ModelServiceConfiguration configuration, ModelServiceClient modelServiceClient) {
        this.configuration = configuration;
        this.modelServiceClient = modelServiceClient;
    }

    @Override
    public Optional<TaxonomyNodeModelData> getNavigationModel(@NotNull SitemapRequestDto requestDto) {
        try {
            return Optional.of(modelServiceClient.getForType(configuration.getNavigationApiUrl(),
                    TaxonomyNodeModelData.class, requestDto.getLocalizationId()));
        } catch (ItemNotFoundInModelServiceException e) {
            log.warn("Cannot find a dynamic navigation in the MS for the request {}", requestDto, e);
            return Optional.empty();
        }
    }

    @NotNull
    @Override
    public Optional<Collection<SitemapItemModelData>> getNavigationSubtree(@NotNull SitemapRequestDto requestDto) {
        try {
            List<SitemapItemModelData> models = Arrays.asList(modelServiceClient.getForType(configuration.getOnDemandApiUrl(),
                    SitemapItemModelData[].class,
                    requestDto.getLocalizationId(),
                    requestDto.getSitemapId(),
                    requestDto.getNavigationFilter().isWithAncestors(),
                    requestDto.getNavigationFilter().getDescendantLevels()));
            return Optional.of(models);
        } catch (ItemNotFoundInModelServiceException e) {
            log.warn("Cannot find items for on-demand dynamic navigation from the MS for the request {}", requestDto, e);
            return Optional.empty();
        }
    }
}
