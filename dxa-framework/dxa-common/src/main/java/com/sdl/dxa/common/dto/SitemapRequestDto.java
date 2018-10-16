package com.sdl.dxa.common.dto;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Value
@Builder(toBuilder = true, builderMethodName = "hiddenBuilder")
@EqualsAndHashCode(exclude = "expandLevels")
@ToString
public class SitemapRequestDto {

    private String sitemapId;

    private int localizationId;

    private DepthCounter expandLevels;

    private NavigationFilter navigationFilter;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, ClaimHolder> claims = new HashMap<>();

    public void addClaim(ClaimHolder holder) {
        if (holder == null) return;
        if (Strings.isNullOrEmpty(holder.getUri())) {
            throw new IllegalArgumentException("Claim should contain an non-empty URI, but was: " + holder);
        }
        claims.put(holder.getUri(), holder);
    }

    public Map<String, ClaimHolder> getClaims() {
        return Collections.unmodifiableMap(claims);
    }

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

    public SitemapRequestDto nextExpandLevel() {
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
