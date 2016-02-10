package com.sdl.webapp.common.api.localization;

/**
 * Site localization - contains information on related localizations for a localization (for example different language
 * variants of the same website).
 */
public interface SiteLocalization {

    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getId();

    /**
     * <p>getPath.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getPath();

    /**
     * <p>getLanguage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getLanguage();

    /**
     * <p>isMaster.</p>
     *
     * @return a boolean.
     */
    boolean isMaster();
}
