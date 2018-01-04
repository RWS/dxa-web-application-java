package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Very generic Entity Model which maps all CM fields to a Settings dictionary (effectively bypassing semantic mapping).
 * @dxa.publicApi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Configuration extends AbstractEntityModel {

    @SemanticProperty("_all")
    private Map<String, String> settings = new HashMap<>();
}
