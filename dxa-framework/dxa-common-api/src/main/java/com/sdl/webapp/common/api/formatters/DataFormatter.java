package com.sdl.webapp.common.api.formatters;

import com.sdl.dxa.modules.core.model.entity.Teaser;

import java.util.List;

/**
 * DataFormatter Interface for that defines the behavior of the formatters
 */
public interface DataFormatter {
    /**
     * <p>score.</p>
     *
     * @return a double.
     */
    double score();

    /**
     * <p>formatData.</p>
     *
     * @param model a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    Object formatData(Object model);

    /**
     * <p>isProcessModel.</p>
     *
     * @return a boolean.
     */
    boolean isProcessModel();

    /**
     * <p>isAddIncludes.</p>
     *
     * @return a boolean.
     */
    boolean isAddIncludes();

    /**
     * <p>getValidTypes.</p>
     *
     * @param allowedTypes a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    List<String> getValidTypes(List<String> allowedTypes);

    /**
     * <p>getSyndicationItemFromTeaser.</p>
     *
     * @param item a {@link Teaser} object.
     * @return a {@link java.lang.Object} object.
     * @throws java.lang.Exception if any.
     */
    Object getSyndicationItemFromTeaser(Teaser item) throws Exception;
}
