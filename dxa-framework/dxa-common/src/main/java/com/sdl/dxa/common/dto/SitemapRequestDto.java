package com.sdl.dxa.common.dto;

import com.sdl.webapp.common.api.navigation.NavigationFilter;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true, builderMethodName = "hiddenBuilder")
public class SitemapRequestDto {

    private String sitemapId;

    private int localizationId;

    private DepthCounter expandLevels;

    private NavigationFilter navigationFilter;

    public static SitemapRequestDtoBuilder wholeTree(int localizationId) {
        return builder(localizationId)
                .navigationFilter(new NavigationFilter().setDescendantLevels(-1))
                .expandLevels(DepthCounter.UNLIMITED_DEPTH);
    }

    public static SitemapRequestDtoBuilder builder(int localizationId) {
        return hiddenBuilder().localizationId(localizationId);
    }

    private static SitemapRequestDtoBuilder hiddenBuilder() {
        return new SitemapRequestDtoBuilder();
    }

    public synchronized SitemapRequestDto nextExpandLevel() {
        return this.toBuilder().expandLevels(new DepthCounter(expandLevels.getCounter() - 1)).build();
    }

    /**
     * Default values for builder.
     */
    public static class SitemapRequestDtoBuilder {

        private DepthCounter expandLevels = DepthCounter.UNLIMITED_DEPTH;

        private NavigationFilter navigationFilter = NavigationFilter.DEFAULT;
    }
}
