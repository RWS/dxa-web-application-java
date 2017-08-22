package com.sdl.dxa.modelservice.service;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EntityModelService {

    /**
     * Loads an Entity model.
     *
     * @param entityRequest entity request data
     * @return an entity model data, never null
     * @throws DxaItemNotFoundException if the component wasn't found
     * @throws ContentProviderException if couldn't load or parse the page content
     */
    @NotNull
    EntityModelData loadEntity(EntityRequestDto entityRequest) throws ContentProviderException;
}
