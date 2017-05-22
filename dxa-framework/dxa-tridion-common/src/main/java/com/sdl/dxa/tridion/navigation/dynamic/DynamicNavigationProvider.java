package com.sdl.dxa.tridion.navigation.dynamic;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Dynamic navigation provider provides access to the navigation based on keywords and pages relations.
 */
@FunctionalInterface
public interface DynamicNavigationProvider {

    /**
     * Loads full navigation model based on a request. Returns an optional in case model is not available but the request is valid.
     *
     * @param requestDto current request
     * @return optional with root navigation model
     */
    Optional<SitemapItemModelData> getNavigationModel(@NotNull SitemapRequestDto requestDto);
}
