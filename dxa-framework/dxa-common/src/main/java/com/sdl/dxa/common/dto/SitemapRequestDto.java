package com.sdl.dxa.common.dto;

import com.sdl.webapp.common.api.navigation.NavigationFilter;
import lombok.Builder;
import lombok.Value;

//todo should control its state (which fields are minimum set)
@Value
@Builder(toBuilder = true)
public class SitemapRequestDto {

    private String sitemapId;

    private int localizationId;

    private DepthCounter expandLevels;

    private NavigationFilter navigationFilter;

    public synchronized SitemapRequestDto nextExpandLevel() {
        return this.toBuilder().expandLevels(new DepthCounter(expandLevels.getCounter() - 1)).build();
    }
}
