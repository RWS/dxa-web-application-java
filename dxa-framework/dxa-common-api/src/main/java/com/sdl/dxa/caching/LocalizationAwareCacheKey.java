package com.sdl.dxa.caching;

import lombok.Value;

import java.io.Serializable;

@Value
public class LocalizationAwareCacheKey implements Serializable {

    private String localizationId;

    private Object key;
}
