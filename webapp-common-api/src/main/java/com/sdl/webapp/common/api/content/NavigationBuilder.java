package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.model.entity.NavigationLinks;

public interface NavigationBuilder {

    NavigationLinks buildTopNavigation() throws ContentProviderException;

    NavigationLinks buildContextNavigation() throws ContentProviderException;

    NavigationLinks buildBreadcrumb() throws ContentProviderException;
}
