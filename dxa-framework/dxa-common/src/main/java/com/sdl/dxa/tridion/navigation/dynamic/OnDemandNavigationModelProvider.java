package com.sdl.dxa.tridion.navigation.dynamic;

import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.dxa.common.dto.SitemapRequestDto;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * On-demand navigation provider provides access to the whole tree or some particular nodes in a navigation model based on the request.
 * Data model from {@code dxa-data-model} aka {@code R2} is used.
 *
 * @since 2.0
 */
@FunctionalInterface
public interface OnDemandNavigationModelProvider {

    /**
     * Returns a collection of navigation model nodes based on the request.
     * If there is just a single parent node for all the nodes from the request, it should merged into one.
     *
     * @param requestDto current request
     * @return optional collection with requested navigation nodes,
     * optional is not present only if the request was correct but nothing wss found
     */
    @NotNull
    Optional<Collection<SitemapItemModelData>> getNavigationSubtree(@NotNull SitemapRequestDto requestDto);
}
