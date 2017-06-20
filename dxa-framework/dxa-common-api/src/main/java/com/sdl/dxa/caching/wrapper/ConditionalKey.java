package com.sdl.dxa.caching.wrapper;

import lombok.Builder;
import lombok.Value;

/**
 * Entity that determines if this key should be cached.
 */
@Value
@Builder
public class ConditionalKey {

    private Object key;

    private boolean skipCaching;
}
