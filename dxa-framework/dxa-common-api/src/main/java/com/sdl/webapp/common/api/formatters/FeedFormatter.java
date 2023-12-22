package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.model.PageModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class to generate Syndication Lists.
 */
@Slf4j
public abstract class FeedFormatter extends BaseFormatter {

    public FeedFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
    }

    /**
     * Accepts {@link PageModel} as a model, and processes it to a feed.
     *
     * @param model object, expected to be assignable from {@link PageModel}
     * @return formatted data
     * @throws IllegalArgumentException if the object is not an instance of {@link PageModel}
     */
    @Override
    @Contract("null -> null; !null -> !null")
    public Object formatData(Object model) {
        if (model == null) {
            return null;
        }
        Assert.isInstanceOf(PageModel.class, model, "Model for this formatter expected to be assignable from PageModel");
        return getData((PageModel) model);
    }

    /**
     * Gets the feed from the a page.
     */
    @Contract("null -> null; !null -> !null")
    protected List<Object> getData(PageModel page) {
        return page == null ? null : getFeedItemsFromPage(page);
    }

    /**
     * Gets the list of syndicated items from a page.
     */
    private List<Object> getFeedItemsFromPage(@NotNull PageModel page) {
        Assert.notNull(page.getRegions(), "Page regions can't be null");

        List<Object> items = new ArrayList<>();

        List<FeedItem> feedItems = page.extractFeedItems();
        for (FeedItem item : feedItems) {
            try {
                items.add(getSyndicationItem(item));
            } catch (Exception e) {
                log.error("Error getting syndication items from {}", item, e);
            }
        }

        return items;
    }
}
