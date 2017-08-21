package com.sdl.dxa.modelservice.service;

import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PageModelService {

    /**
     * Loads a page from CD and converts it to the {@link PageModelData} doing all the model processing.
     */
    @NotNull
    PageModelData loadPageModel(PageRequestDto pageRequest) throws ContentProviderException;
}
