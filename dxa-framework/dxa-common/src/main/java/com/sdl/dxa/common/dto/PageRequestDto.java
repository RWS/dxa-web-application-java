package com.sdl.dxa.common.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.util.Assert;

/**
 * Data transfer object (DTO) for page requests.
 * Fields {@code path} nad {@code publication ID} are guaranteed to be set.
 */
@Builder(toBuilder = true, builderMethodName = "hiddenBuilder")
@Value
@EqualsAndHashCode(exclude = "depthCounter")
public class PageRequestDto {

    private int publicationId;

    private int pageId;

    private String path;

    private String uriType;

    private PageInclusion includePages;

    private ContentType contentType;

    private DataModelType dataModelType;

    private int expansionDepth;

    private DepthCounter depthCounter;

    public static PageRequestDtoBuilder builder(String publicationId, int pageId) {
        return builder(Integer.valueOf(publicationId), pageId);
    }

    public static PageRequestDtoBuilder builder(int publicationId, int pageId) {
        return hiddenBuilder().publicationId(publicationId).pageId(pageId);
    }

    public static PageRequestDtoBuilder builder(String publicationId, String path) {
        return builder(Integer.valueOf(publicationId), path);
    }

    public static PageRequestDtoBuilder builder(int publicationId, String path) {
        return hiddenBuilder().publicationId(publicationId).path(path);
    }

    private static PageRequestDtoBuilder hiddenBuilder() {
        return new PageRequestDtoBuilder();
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

        private PageRequestDtoBuilder() {
        }

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
