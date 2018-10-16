package com.sdl.dxa.modelservice.service;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import org.jetbrains.annotations.NotNull;

public interface ModelServiceProvider extends PageModelService, EntityModelService {

    String loadPageContent(PageRequestDto pageRequest) throws ContentProviderException;

    EntityModelData loadEntity(String publicationId, @NotNull String entityId) throws ContentProviderException;

}
