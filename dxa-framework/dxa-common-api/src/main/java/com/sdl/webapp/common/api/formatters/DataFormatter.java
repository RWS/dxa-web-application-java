package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.model.entity.Teaser;

import java.util.List;

/**
 * DataFormatter Interface for that defines the behavior of the formatters
 */
public interface DataFormatter {
    double score();

    Object formatData(Object model);

    boolean isProcessModel();

    boolean isAddIncludes();

    List<String> getValidTypes(List<String> allowedTypes);

    Object getSyndicationItemFromTeaser(Teaser item) throws Exception;
}
