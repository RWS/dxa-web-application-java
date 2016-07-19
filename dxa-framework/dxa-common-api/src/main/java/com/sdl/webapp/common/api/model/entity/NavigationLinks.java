package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class NavigationLinks extends AbstractEntityModel {

    @JsonProperty("Items")
    private List<Link> items;

    /**
     * <p>Constructor for NavigationLinks.</p>
     */
    public NavigationLinks() {
    }

    /**
     * <p>Constructor for NavigationLinks.</p>
     *
     * @param items a {@link java.util.List} object.
     */
    public NavigationLinks(List<Link> items) {
        this.items = items;
    }
}
