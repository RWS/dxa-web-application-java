package com.sdl.dxa.caching;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * Combined cache key with a lozalition ID and key itself.
 *
 * @dxa.publicApi
 */
@Value
@EqualsAndHashCode
public class LocalizationAwareCacheKey implements Serializable {

    private String localizationId;

    private Serializable key;
}
