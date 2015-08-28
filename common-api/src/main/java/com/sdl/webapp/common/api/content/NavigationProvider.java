package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.NavigationLinks;
import com.sdl.webapp.common.api.model.entity.SitemapItem;

/**
 * Navigation provider.
 */
public interface NavigationProvider {

    /**
     * Gets the navigation model for the specified localization.
     *
     * @param localization The localization.
     * @return The navigation model as a {@code SitemapItem}.
     * @throws NavigationProviderException If an error occurred so that the navigation model could not be retrieved.
     */
    SitemapItem getNavigationModel(Localization localization) throws NavigationProviderException;

    /**
     * Gets navigation links for the top navigation menu for the specified request path.
     *
     * @param requestPath The request path.
     * @param localization The localization.
     * @return A {@code NavigationLinks} object containing navigation links.
     * @throws NavigationProviderException If an error occurred so that the navigation links could not be retrieved.
     */
    NavigationLinks getTopNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException;

    /**
     * Gets navigation links for the context navigation panel for the specified request path.
     *
     * @param requestPath The request path.
     * @param localization The localization.
     * @return A {@code NavigationLinks} object containing navigation links.
     * @throws NavigationProviderException If an error occurred so that the navigation links could not be retrieved.
     */
    NavigationLinks getContextNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException;

    /**
     * Gets navigation links for the breadcrumb bar for the specified request path.
     *
     * @param requestPath The request path.
     * @param localization The localization.
     * @return A {@code NavigationLinks} object containing navigation links.
     * @throws NavigationProviderException If an error occurred so that the navigation links could not be retrieved.
     */
    NavigationLinks getBreadcrumbNavigationLinks(String requestPath, Localization localization)
            throws NavigationProviderException;
}
