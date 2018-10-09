package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.caching.LocalizationAwareCacheKey;
import com.sdl.webapp.common.api.model.EntityModel;
import org.springframework.stereotype.Component;

/**
 * Default implementation of entities cache for manual access.
 *
 * @dxa.publicApi
 */
@Component
public class EntitiesCache extends SimpleCacheWrapper<EntityModelData, EntityModel> {

    @Override
    public String getCacheName() {
        return "entities";
    }

    @Override
    public Class<EntityModel> getValueType() {
        return EntityModel.class;
    }

    @Override
    public LocalizationAwareCacheKey getSpecificKey(EntityModelData entityModelData, Object... other) {
        if (other.length == 0 || (other.length == 1 && other[0] == null) ){
            return getKey(entityModelData.getId(), entityModelData.getSchemaId(), entityModelData.getMvcData());
        }
        return getKey(entityModelData.getId(), entityModelData.getSchemaId(), entityModelData.getMvcData(),other);
    }
}
