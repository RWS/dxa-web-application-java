package com.sdl.dxa.caching;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.io.Serializable;

/**
 * Combined cache key with a localization ID and key itself.
 *
 * @dxa.publicApi
 */
@Value
@EqualsAndHashCode
@ToString(exclude = "key")
public class LocalizationAwareCacheKey implements Serializable {

    private String localizationId;

    private Serializable key;
}
