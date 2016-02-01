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
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
/**
 * <p>DefaultRegionBuilder class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class DefaultRegionBuilder implements RegionBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRegionBuilder.class);

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
            if (!Strings.isNullOrEmpty(regionName)) {
                RegionModel region = regions.get(regionName);
                MvcData regionMvcData = callback.getRegionMvcData(source);
                if (region == null) {
                    LOG.debug("Creating region: {}", regionName);
                    try {
                        Class regionModelType = viewModelRegistry.getViewModelType(regionMvcData);
                        try {
                            region = (RegionModel) regionModelType.getDeclaredConstructor(String.class).newInstance(regionName);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            throw new ContentProviderException(e);
                        }
                    } catch (DxaException e) {
                        throw new ContentProviderException(e);
                    }

                    region.setMvcData(regionMvcData);

                    regions.add(region);
                }
                if (conditionalEntityEvaluator == null || conditionalEntityEvaluator.includeEntity(entity)) {
                    region.addEntity(entity);
                }

            }
        }
        return regions;
    }
}
