package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class LinkList extends AbstractEntityModel {

    @JsonProperty("Headline")
    private String headline;

    @JsonProperty("Links")
    private List<Link> links;
}
