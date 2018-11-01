package com.sdl.dxa.common.dto;

import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Value
@Builder(toBuilder = true, builderMethodName = "hiddenBuilder")
@ToString
public class StaticContentRequestDto {

    private String binaryPath;

    private String localizationId;

    private String localizationPath;

    private String baseUrl;

    private boolean noMediaCache;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, ClaimHolder> claims = new HashMap<>();

    public static StaticContentRequestDtoBuilder builder(String binaryPath, String localizationId) {
        return hiddenBuilder().localizationId(localizationId).binaryPath(binaryPath);
    }

    private static StaticContentRequestDtoBuilder hiddenBuilder() {
        return new StaticContentRequestDtoBuilder();
    }

    /**
     * Checks if localization path is set.
     *
     * @return whether the localization path is not null
     */
    public boolean isLocalizationPathSet() {
        return localizationPath != null;
    }

    public static class StaticContentRequestDtoBuilder {

        private String baseUrl = "http://localhost/";
    }

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

}
