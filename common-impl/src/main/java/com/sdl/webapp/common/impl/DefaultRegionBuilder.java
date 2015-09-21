package com.sdl.webapp.common.impl;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.DefaultImplementation;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.RegionBuilderCallback;
import com.sdl.webapp.common.api.content.RegionBuilder;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;

import com.sdl.webapp.common.exceptions.DxaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
                                       Localization localization) throws ContentProviderException {

        RegionModelSet regions = new RegionModelSetImpl();
        for (Object source : sourceList) {
            final EntityModel entity = callback.buildEntity(source, localization);

            String regionName = callback.getRegionName(source);
            if (!Strings.isNullOrEmpty(regionName)) {
                RegionModelImpl region = (RegionModelImpl) regions.get(regionName);
                if (region == null) {
                    LOG.debug("Creating region: {}", regionName);
                    try {
                        region = new RegionModelImpl(regionName);
                    } catch (DxaException e) {
                        e.printStackTrace();
                    }

                    region.setMvcData(callback.getRegionMvcData(source));

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
