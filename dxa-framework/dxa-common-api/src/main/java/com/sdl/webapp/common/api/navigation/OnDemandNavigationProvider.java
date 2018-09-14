package com.sdl.webapp.common.api.navigation;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Supports "on-demand" navigation (for example, can provide navigation subtrees).
 * @dxa.publicApi
 */
@FunctionalInterface
public interface OnDemandNavigationProvider {

    /**
     * Gets a Navigation subtree for the given Sitemap Item.
     *
     * @param sitemapItemId    the context identifier of a {@link SitemapItem}, nullable
     * @param navigationFilter the {@link NavigationFilter} used to specify which information to put in the subtree
     * @param localization     context localization
     * @return collection of {@link SitemapItem} representing the requested subtree, never returns <code>null</code>
     * @throws DxaItemNotFoundException if such item is not found
     */
    @Contract("_, _, _ -> !null")
    Collection<SitemapItem> getNavigationSubtree(@Nullable String sitemapItemId, @NonNull NavigationFilter navigationFilter, @NonNull Localization localization) throws DxaItemNotFoundException;
}
