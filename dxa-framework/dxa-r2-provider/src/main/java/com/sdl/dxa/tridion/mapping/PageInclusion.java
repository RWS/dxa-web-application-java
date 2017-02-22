package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.tridion.mapping.impl.ModelBuilderPipelineImpl;

/**
 * Indicates whether Include Page Regions should be included.
 *
 * @see ModelBuilderPipelineImpl
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
