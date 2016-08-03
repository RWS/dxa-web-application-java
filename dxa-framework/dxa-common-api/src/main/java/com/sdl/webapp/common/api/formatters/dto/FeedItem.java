package com.sdl.webapp.common.api.formatters.dto;

import com.sdl.webapp.common.api.formatters.FeedFormatter;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.Link;
import lombok.Data;

import java.util.Date;

/**
 * Feed item class is used to hold the data for {@link FeedFormatter}.
 */
@Data
public class FeedItem {

    private String headline;

    private Link link;

    private RichText summary;

    private Date date;
}
