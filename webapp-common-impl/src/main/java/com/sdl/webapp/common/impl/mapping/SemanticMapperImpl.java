package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.model.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@code SemanticMapper}.
 */
@Component
public class SemanticMapperImpl implements SemanticMapper {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticMapperImpl.class);

    private SemanticInfoRegistry semanticInfoRegistry = new SemanticInfoRegistry();

    @Override
    public Entity createEntity(Class<? extends Entity> entityClass) throws SemanticMappingException {
        LOG.trace("createEntity: {}", entityClass);

        final Entity entity = createEntityInstance(entityClass);

        // TODO: Hoe gaan we dit doen? In de .NET versie wordt gewoon over alle property infos heengelopen van alle
        // @SemanticEntity infos, en als er dubbelen zijn dan wint degene die toevallig als laatste is. Dat ziet er
        // niet helemaal correct uit. Is er een manier om te bepalen welke SemanticEntityInfo we willen gebruiken?
        // Bevat het component op de page bijvoorbeeld info hierover?

        for (SemanticEntityInfo entityInfo : semanticInfoRegistry.getEntityInfo(entityClass)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("entityName: {}, vocabulary: {}", entityInfo.getEntityName(), entityInfo.getVocabulary());
            }

            // TODO: match met wat in de localization zit



            for (SemanticPropertyInfo propertyInfo : entityInfo.getPropertyInfo()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("propertyName: {}, field: {}", propertyInfo.getPropertyName(), propertyInfo.getField());
                }

                // TODO


            }
        }




        return entity;
    }

    private Entity createEntityInstance(Class<? extends Entity> entityClass) throws SemanticMappingException {
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SemanticMappingException("Exception while instantiating entity of type: " + entityClass.getName(), e);
        }
    }
}
