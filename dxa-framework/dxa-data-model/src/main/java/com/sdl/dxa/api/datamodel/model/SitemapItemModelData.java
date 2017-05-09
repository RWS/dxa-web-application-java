package com.sdl.dxa.api.datamodel.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

import java.util.List;

@Data
@Accessors(chain = true)
public class SitemapItemModelData {

    private String id;

    private String type;

    private String title;

    private String url;

    private boolean visible;

    private List<SitemapItemModelData> items;

    private DateTime publishedDate;
}
