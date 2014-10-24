package com.sdl.webapp.dd4t.entityfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@code EntityFactoryRegistry}.
 */
@Component
public class DD4TEntityFactoryRegistryImpl implements DD4TEntityFactoryRegistry {

    private final Map<Class<?>, DD4TEntityFactory> entityFactoryMap = new HashMap<>();

    @Autowired
    public DD4TEntityFactoryRegistryImpl(List<DD4TEntityFactory> entityFactoryList) {
        for (DD4TEntityFactory factory : entityFactoryList) {
            for (Class<?> type : factory.supportedEntityTypes()) {
                entityFactoryMap.put(type, factory);
            }
        }
    }

    @Override
    public DD4TEntityFactory getFactoryFor(Class<?> entityClass) throws UnsupportedEntityTypeException {
        final DD4TEntityFactory factory = entityFactoryMap.get(entityClass);
        if (factory == null) {
            throw new UnsupportedEntityTypeException("No factory for creating entity of type: " + entityClass.getName());
        }

        return factory;
    }
}
