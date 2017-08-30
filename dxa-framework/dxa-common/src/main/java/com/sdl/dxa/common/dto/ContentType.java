package com.sdl.dxa.common.dto;

/**
 * Way you expect the content to be.
 * Handy to have it in DTO to support key generation for caching even if you do not override methods, but use different names,
 * so that the same requests with different expected return content type won't clash on the same return type.
 */
public enum ContentType {
    RAW, MODEL
}
