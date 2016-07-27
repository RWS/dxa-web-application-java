package com.sdl.webapp.common.api.formatters;

import com.sdl.dxa.modules.core.model.entity.Teaser;

import java.util.List;

/**
 * DataFormatter Interface for that defines the behavior of the formatters.
 */
public interface DataFormatter {

    /**
     * Gets the score depending on the media type.
     */
    double score();

    /**
     * Returns the formatted data with possible additional model processing.
     */
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
     * Gets a syndication Entry from a teaser.
     */
    Object getSyndicationItemFromTeaser(Teaser item) throws Exception;
}
