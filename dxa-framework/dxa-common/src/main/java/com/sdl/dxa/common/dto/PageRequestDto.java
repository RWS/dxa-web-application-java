package com.sdl.dxa.common.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.util.Assert;

/**
 * Data transfer object (DTO) for page requests.
 */
@Builder(toBuilder = true)
@Value
@EqualsAndHashCode(exclude = "depthCounter")
public class PageRequestDto {

    private int publicationId;

    private String uriType;

    private String path;

    private PageInclusion includePages;

    private ContentType contentType;

    private DataModelType dataModelType;

    private int expansionDepth;

    private DepthCounter depthCounter;

    /**
     * Way you expect the content to be.
     * Handy to have it in DTO to support key generation for caching even if you do not override methods, but use different names,
     * so that the same requests with different expected return content type won't clash on the same return type.
     */
    public enum ContentType {
        RAW, MODEL
    }

    public enum DataModelType {
        R2, DD4T
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

        private DataModelType dataModelType = DataModelType.R2;

        private int expansionDepth = 100;

        private DepthCounter depthCounter = new DepthCounter(expansionDepth);

        public PageRequestDtoBuilder expansionDepth(int expansionDepth) {
            Assert.isTrue(expansionDepth > 0, "Expansion depth should be a positive number");
            depthCounter(new DepthCounter(expansionDepth));
            this.expansionDepth = expansionDepth;
            return this;
        }

        private PageRequestDtoBuilder depthCounter(DepthCounter depthCounter) {
            this.depthCounter = depthCounter;
            return this;
        }
    }
}
