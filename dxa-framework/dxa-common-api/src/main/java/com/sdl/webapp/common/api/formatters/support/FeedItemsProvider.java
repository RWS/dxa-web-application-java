package com.sdl.webapp.common.api.formatters.support;

import java.util.List;

/**
 * Indicates that an implementor can generate a list of {@link FeedItem} out of its data.
 *
 * @dxa.publicApi
 */
@FunctionalInterface
public interface FeedItemsProvider {

    List<FeedItem> extractFeedItems();
}
