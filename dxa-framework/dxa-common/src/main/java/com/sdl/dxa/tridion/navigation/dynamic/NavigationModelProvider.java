package com.sdl.dxa.tridion.navigation.dynamic;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Navigation provider provides access to the navigation model of the publication.
 * Data model from {@code dxa-data-model} aka {@code R2} is used.
 *
 * @since 2.0
 */
@FunctionalInterface
public interface NavigationModelProvider {

    /**
     * Loads full navigation model based on a request. Returns an optional in case model is not available but the request is valid.
     *
     * @param requestDto current request with mandatory localization ID
     * @return optional with root navigation model
     */
    Optional<SitemapItemModelData> getNavigationModel(@NotNull SitemapRequestDto requestDto);
}
