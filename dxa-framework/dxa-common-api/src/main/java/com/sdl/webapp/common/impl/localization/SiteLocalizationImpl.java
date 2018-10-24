package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.SiteLocalization;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SiteLocalizationImpl implements SiteLocalization {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("Language")
    private String language;

    @JsonProperty("IsMaster")
    private boolean master;
}
