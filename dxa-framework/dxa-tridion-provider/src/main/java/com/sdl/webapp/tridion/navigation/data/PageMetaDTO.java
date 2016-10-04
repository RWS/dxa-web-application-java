package com.sdl.webapp.tridion.navigation.data;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

/**
 * Pure DTO class for Tridion Taxonomies PageMeta data object.
 */
@Value
@Builder
public class PageMetaDTO {

    private int id;

    private String title;

    private String url;

    private Date publishedDate;
}
