package com.sdl.webapp.common.impl;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.RegionBuilder;
import com.sdl.webapp.common.api.content.RegionBuilderCallback;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
@Slf4j
//todo dxa2 move to appropriate place
public class DefaultRegionBuilder implements RegionBuilder {

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private ConditionalEntityEvaluator conditionalEntityEvaluator;

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionModelSet buildRegions(PageModel page, List<?> sourceList, RegionBuilderCallback callback,
                                       Localization localization) throws ContentProviderException {

        RegionModelSet regions = new RegionModelSetImpl();
        for (Object source : sourceList) {
            final EntityModel entity = callback.buildEntity(source, localization);

            String regionName = callback.getRegionName(source);
            if (Strings.isNullOrEmpty(regionName)) {
                continue;
            }

            RegionModel region = regions.containsName(regionName) ?
                    regions.get(regionName) : createRegionModel(page, callback, source, regionName);

            if (region == null) {
                continue;
            }

            if (regions.add(region)) {
                log.debug("Added new region {} to a model set", region);
            }

            if (conditionalEntityEvaluator == null || conditionalEntityEvaluator.includeEntity(entity)) {
                region.addEntity(entity);
            }
        }
        return regions;
    }

    private RegionModel createRegionModel(PageModel page, RegionBuilderCallback callback, Object source, String regionName) throws ContentProviderException {
        log.debug("Creating region: {}", regionName);
        try {
            MvcData regionMvcData = callback.getRegionMvcData(source);
            Class<? extends ViewModel> regionModelType = viewModelRegistry.getViewModelType(regionMvcData);

            if (regionModelType == null) {
                log.warn("Could not find a model type for region {} with MvcData {}", regionName, regionMvcData);
                if (page.getRegions() != null && page.getRegions().containsName(regionName)) {
                    RegionModel predefined = page.getRegions().get(regionName);
                    log.debug("Try to use a predefined region {}", predefined);
                    regionModelType = viewModelRegistry.getViewModelType(predefined.getMvcData());
                }
            }

            if (regionModelType == null) {
                log.error("Could not find a model type for region {} even using a predefined region", regionName);
                return null;
            }

            RegionModel region = (RegionModel) regionModelType.getDeclaredConstructor(String.class).newInstance(regionName);
            region.setMvcData(regionMvcData);
            return region;
        } catch (DxaException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ContentProviderException(e);
        }
    }
}
