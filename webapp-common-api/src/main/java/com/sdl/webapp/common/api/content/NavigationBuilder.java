package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;

public interface NavigationBuilder {

    NavigationLinks buildTopNavigation(String requestPath, Localization localization) throws ContentProviderException;

    NavigationLinks buildContextNavigation(String requestPath, Localization localization)
            throws ContentProviderException;

    NavigationLinks buildBreadcrumb(String requestPath, Localization localization) throws ContentProviderException;
}
