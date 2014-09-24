package com.sdl.tridion.referenceimpl.common.config;

import com.sdl.tridion.referenceimpl.common.model.Entity;

/**
 * TODO: Documentation.
 */
public interface ViewModelRegistry {

    Class<? extends Entity> getEntityViewModelType(String viewName);
}
