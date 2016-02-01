package com.sdl.webapp.common.api.mapping.semantic;

import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.ViewModel;

import java.util.Map;

/**
 * <p>SemanticMapper interface.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface SemanticMapper {

    /**
     * Creates an entity of the specified type and fills the fields of the entity by performing semantic mapping.
     *
     * @param entityClass       The type of the entity to be created.
     * @param semanticFields    The semantic fields to be used when performing semantic mapping.
     * @param fieldDataProvider A field data provider which provides the actual data for the fields when they are
     *                          mapped.
     * @param <T>               entity class
     * @return An entity of the specified type, in which the fields are filled with data provided by the field data
     * provider.
     * @throws com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException When an error occurs.
     */
    <T extends ViewModel> T createEntity(Class<? extends T> entityClass,
                                         Map<FieldSemantics, SemanticField> semanticFields,
                                         SemanticFieldDataProvider fieldDataProvider)
            throws SemanticMappingException;

}
