package com.sdl.dxa.api.model.data.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.sdl.dxa.DxaSpringInitialization;

/**
 * Mix-in for Jackson that makes everything polymorphic.
 * <p>Is added to the default configuration of {@link ObjectMapper} in {@link DxaSpringInitialization} intentionally making
 * all Java objects polymorphic, so we can handle them with custom {@link ModelDataTypeIdResolver} and {@link ModelDataTypeResolver}.</p>
 */
@SuppressWarnings("unused")
@JsonTypeResolver(ModelDataTypeResolver.class)
@JsonTypeIdResolver(ModelDataTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "$type")
public interface PolymorphicObjectMixin {

}
