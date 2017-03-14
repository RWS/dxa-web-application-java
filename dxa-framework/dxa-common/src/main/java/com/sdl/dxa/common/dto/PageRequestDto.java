package com.sdl.dxa.common.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Data transfer object (DTO) for page requests.
 */
@Builder
@Value
public class PageRequestDto {

    private int publicationId;

    private String uriType;

    private String path;

    private PageInclusion includePages;

    private ContentType contentType;

    /**
     * Way you expect the content to be.
     * Handy to have it in DTO to support key generation for caching even if you do not override methods, but use different names,
     * so that the same requests with different expected return content type won't clash on the same return type.
     */
    public enum ContentType {
        RAW, MODEL
    }

    /**
     * Indicates whether Include Page Regions should be included.
     */
    public enum PageInclusion {
        /**
         * Page regions should be included.
         */
        INCLUDE,

        /**
         * Page regions should be excluded.
         */
        EXCLUDE
    }

    /**
     * Default values for builder.
     */
    @SuppressWarnings("unused")
    public static class PageRequestDtoBuilder {

        private String uriType = "tcm";

        private PageInclusion includePages = PageInclusion.INCLUDE;

        private ContentType contentType = ContentType.MODEL;
    }
}
