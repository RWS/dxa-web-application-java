package com.sdl.tridion.referenceimpl.dd4t.entityfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@code EntityFactoryRegistry}.
 */
@Component
public class EntityFactoryRegistryImpl implements EntityFactoryRegistry {

    private final Map<Class<?>, EntityFactory> entityFactoryMap = new HashMap<>();

    @Autowired
    public EntityFactoryRegistryImpl(List<EntityFactory> entityFactoryList) {
        for (EntityFactory factory : entityFactoryList) {
            for (Class<?> type : factory.supportedEntityTypes()) {
                entityFactoryMap.put(type, factory);
            }
        }
    }

    @Override
    public EntityFactory getFactoryFor(Class<?> entityType) {
        return entityFactoryMap.get(entityType);
    }
}
