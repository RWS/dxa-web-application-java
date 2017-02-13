package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.tridion.mapping.impl.ModelBuilderPipeline;

/**
 * Indicates whether Include Page Regions should be included.
 *
 * @see ModelBuilderPipeline
 * @see PageModelBuilder
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
