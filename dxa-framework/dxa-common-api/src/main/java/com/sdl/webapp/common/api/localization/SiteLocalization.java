package com.sdl.webapp.common.api.localization;

/**
 * Site localization - contains information on related localizations for a localization (for example different language
 * variants of the same website).
 */
public interface SiteLocalization {

    String getId();

    String getPath();

    String getLanguage();

    boolean isMaster();
}
