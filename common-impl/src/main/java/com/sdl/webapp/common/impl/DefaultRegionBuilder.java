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
import com.sdl.webapp.common.api.model.region.RegionImpl;

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
    public Map<String,RegionModel> buildRegions(PageModel page,
			   							   ConditionalEntityEvaluator conditionalEntityEvaluator,
                                           List<?> sourceList,
                                           RegionBuilderCallback callback,
                                           Localization localization) throws ContentProviderException {

        Map<String,RegionModel> regions = new HashMap<>();
        for (Object source : sourceList) {
            final EntityModel entity = callback.buildEntity(source, localization);

            String regionName = callback.getRegionName(source);
            if (!Strings.isNullOrEmpty(regionName)) {
                RegionImpl region = (RegionImpl) regions.get(regionName);
                if (region == null) {
                    LOG.debug("Creating region: {}", regionName);
                    region = new RegionImpl();

                    region.setName(regionName);
                    region.setMvcData(callback.getRegionMvcData(source));

                    regions.put(regionName, region);
                }
                if (conditionalEntityEvaluator == null || conditionalEntityEvaluator.IncludeEntity(entity))
                {
                	region.addEntity(entity);
                }
                
            }
        }
        return regions;
    }
}
