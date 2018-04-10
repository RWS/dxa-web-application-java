package com.sdl.dxa.caching;

import lombok.Builder;
import lombok.Value;

/**
 * Key that determines if the value with this key may be cached. Used for dynamic refusal of caching.
 *
 * @dxa.publicApi
 */
@Value
@Builder
public class ConditionalKey {

    private LocalizationAwareCacheKey key;

    private boolean skipCaching;
}
