package com.sdl.webapp.tridion.navigation.data;

import lombok.Builder;
import lombok.Value;

/**
 * Pure DTO class for Tridion Taxonomies Keyword data object.
 */
@Value
@Builder
public class KeywordDTO {

    private String keywordUri;

    private String taxonomyUri;

    private String name;

    private String key;

    private boolean withChildren;

    private int referenceContentCount;

    private String description;

    private boolean keywordAbstract;
}
