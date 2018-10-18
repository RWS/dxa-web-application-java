package com.sdl.dxa.modelservice.service;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import org.jetbrains.annotations.NotNull;

public interface ModelServiceProvider extends PageModelService, EntityModelService {

    /**
     * Loads a page from Content service and returns its raw representation.
     *
     * @param pageRequest page request dto
     * @return raw representation of page
     * @throws ContentProviderException
     */
    String loadPageContent(PageRequestDto pageRequest) throws ContentProviderException;

    /**
     * Loads an Entity model from Content service by given publication id and entity id.
     *
     * @param publicationId publication id
     * @param entityId      entity id
     * @return EntityModelData representation
     * @throws ContentProviderException
     */
    EntityModelData loadEntity(String publicationId, @NotNull String entityId) throws ContentProviderException;

}
