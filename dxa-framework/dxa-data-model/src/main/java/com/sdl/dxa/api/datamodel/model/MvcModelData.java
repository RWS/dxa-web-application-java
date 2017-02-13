package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;

import java.util.Map;

@Value
@JsonTypeName
public class MvcModelData {

    private String actionName;

    private String areaName;

    private String controllerAreaName;

    private String controllerName;

    private String viewName;

    private Map<String, String> parameters;
}
