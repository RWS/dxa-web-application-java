package com.sdl.dxa.caching;

import lombok.Builder;
import lombok.Value;

/**
 * Entity that determines if this key should be cached.
 */
@Value
@Builder
public class ConditionalKey {

    private LocalizationAwareCacheKey key;

    private boolean skipCaching;
}
