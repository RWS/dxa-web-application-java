package com.sdl.dxa.common.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true, builderMethodName = "hiddenBuilder")
public class StaticContentRequestDto {

    private String binaryPath;

    private String localizationId;

    private String localizationPath;

    private String baseUrl;

    private boolean noMediaCache;

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
}
