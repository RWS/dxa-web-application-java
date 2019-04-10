package com.sdl.dxa;

import com.sdl.webapp.common.api.mapping.views.AbstractModuleInitializer;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModel;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModels;
import com.sdl.webapp.common.api.model.entity.GenericTopic;

/**
 * In order to work with Tridion Docs content, it is necessary to register GenericTopic as a View.
 * A DXA Web Application/Module that wants to work with Tridion Docs content should include this module
 * unless it defines its own View Model Type for generic Topics.
 *
 * This module is enabled by default in {@link DxaSpringInitialization} it can be disabled by enabling
 * the Spring profile "dxa.generictopic.disabled"
 */
@RegisteredViewModels({
        @RegisteredViewModel(viewName = "Topic", modelClass = GenericTopic.class)
})
public class IshModuleInitializer extends AbstractModuleInitializer {

    @Override
    protected String getAreaName() {
        return "ish";
    }
}
