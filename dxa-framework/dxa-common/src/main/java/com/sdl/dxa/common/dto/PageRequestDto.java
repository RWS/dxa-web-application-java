package com.sdl.dxa.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.util.Assert;

/**
 * Data transfer object (DTO) for page requests.
 */
@Builder
@Value
@EqualsAndHashCode(exclude = "depthCounter")
public class PageRequestDto {

    private int publicationId;

    private String uriType;

    private String path;

    private PageInclusion includePages;

    private ContentType contentType;

    private int expansionDepth;

    private DepthCounter depthCounter;

    public boolean depthIncreaseAndCheckIfSafe() {
        return this.depthCounter.depthIncrease() > 0;
    }

    public void depthDecrease() {
        this.depthCounter.depthDecrease();
    }

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

    @AllArgsConstructor
    private static class DepthCounter {

        private int counter;

        private int depthIncrease() {
            return --counter;
        }

        private void depthDecrease() {
            counter++;
        }
    }

    /**
     * Default values for builder.
     */
    @SuppressWarnings("unused")
    public static class PageRequestDtoBuilder {

        private String uriType = "tcm";

        private PageInclusion includePages = PageInclusion.INCLUDE;

        private ContentType contentType = ContentType.MODEL;

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
