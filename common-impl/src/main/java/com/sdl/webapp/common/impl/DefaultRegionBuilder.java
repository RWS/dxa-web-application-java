package com.sdl.webapp.common.impl;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.DefaultImplementation;
import com.sdl.webapp.common.api.content.*;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.*;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;

import com.sdl.webapp.common.exceptions.DxaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DefaultRegionBuilder
 *
 * @author nic
 */
@Component
public class DefaultRegionBuilder extends DefaultImplementation<RegionBuilder> implements RegionBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRegionBuilder.class);

    @Override
    public Class<?> getObjectType() {
        return RegionBuilder.class;
    }

    @Override
    public RegionModelSet buildRegions(PageModel page,
                                       ConditionalEntityEvaluator conditionalEntityEvaluator,
                                       List<?> sourceList,
                                       RegionBuilderCallback callback,
                                       Localization localization,
                                       ViewModelRegistry viewModelRegistry) throws ContentProviderException {

        RegionModelSet regions = new RegionModelSetImpl();
        for (Object source : sourceList) {
            final EntityModel entity = callback.buildEntity(source, localization);

            String regionName = callback.getRegionName(source);
            if (!Strings.isNullOrEmpty(regionName)) {
                RegionModel region = regions.get(regionName);
                MvcData regionMvcData = callback.getRegionMvcData(source);
                if (region == null) {
                    LOG.debug("Creating region: {}", regionName);
                    try {
                        //region = new RegionModelImpl(regionName);
                        Class regionModelType = viewModelRegistry.getViewModelType(regionMvcData);
                        try {
                            region = (RegionModel) regionModelType.getDeclaredConstructor(String.class).newInstance(regionMvcData.getViewName());
                        } catch (InstantiationException | IllegalAccessException |InvocationTargetException | NoSuchMethodException e ) {
                            throw new ContentProviderException(e);
                        }
                    } catch (DxaException e) {
                        throw new ContentProviderException(e);
                    }

                    region.setMvcData(regionMvcData);

                    regions.add(region);
                }
                if (conditionalEntityEvaluator == null || conditionalEntityEvaluator.IncludeEntity(entity)) {
                    region.addEntity(entity);
                }

            }
        }
        return regions;
    }
}
