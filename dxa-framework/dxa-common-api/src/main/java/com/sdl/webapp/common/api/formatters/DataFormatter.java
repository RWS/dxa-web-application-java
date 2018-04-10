package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.formatters.support.FeedItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * DataFormatter Interface for that defines the behavior of the formatters.
 * @dxa.publicApi
 */
public interface DataFormatter {

    /**
     * Gets the score depending on the media type.
     */
    double score();

    /**
     * Returns the formatted data with possible additional model processing.
     */
    @Nullable
    Object formatData(Object model);

    /**
     * Says whether model processing is required.
     */
    boolean isProcessModel();

    /**
     * Says whether to to add includes.
     */
    boolean isAddIncludes();

    /**
     * Gets the valid media types depending on the allowed types by the formatter.
     *
     * @param allowedTypes list of allowed types, nothing but from this list will be in result
     * @return list of valid types which are also in allowed list
     */
    List<String> getValidTypes(List<String> allowedTypes);

    /**
     * Gets a syndication item from a {@link FeedItem}. The class differs per type of formatter.
     *
     * @param item POJO containing data for creating a syndication entry
     */
    Object getSyndicationItem(FeedItem item);
}
