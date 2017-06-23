package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.webapp.common.api.model.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class EntitiesCache extends SimpleCacheWrapper<EntityModelData, EntityModel> {

    @Override
    public String getCacheName() {
        return "entities";
    }

    @Override
    public Object getSpecificKey(EntityModelData entityModelData, Object... other) {
        return getKey(entityModelData.getId(), entityModelData.getSchemaId(), entityModelData.getMvcData());
    }
}
