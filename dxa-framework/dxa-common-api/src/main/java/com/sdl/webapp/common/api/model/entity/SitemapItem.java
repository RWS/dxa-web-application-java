package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SitemapItem extends AbstractEntityModel {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Url")
    private String url;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Items")
    private List<SitemapItem> items;

    @JsonProperty("PublishedDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private DateTime publishedDate;

    @JsonProperty("Visible")
    private boolean visible;
}
