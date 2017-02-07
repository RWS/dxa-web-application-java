package com.sdl.dxa.api.model.data;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;

import java.util.Map;

@Value
@JsonTypeName
public class MvcData {

    private String actionName;

    private String areaName;

    private String controllerAreaName;

    private String controllerName;

    private String viewName;

    private Map<String, String> parameters;
}
