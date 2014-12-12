package com.sdl.webapp.common.api.localization;

import com.sdl.webapp.common.api.mapping.config.SemanticSchema;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Localization.
 */
public interface Localization {

    /**
     * Gets the ID of this localization.
     *
     * @return The ID of this localization.
     */
    String getId();

    /**
     * Gets the path of this localization.
     *
     * @return The path of this localization.
     */
    String getPath();

    /**
     * Determines if the specified URL refers to static content in this localization.
     *
     * @param url The URL.
     * @return {@code true} if the specified URL refers to static content in this localization, {@code false} otherwise.
     */
    boolean isStaticContent(String url);

    /**
     * Returns {@code true} when this is the default localization, {@code false} otherwise.
     *
     * @return {@code true} when this is the default localization, {@code false} otherwise.
     */
    boolean isDefault();

    /**
     * Returns {@code true} if this localization is in staging mode, {@code false} otherwise.
     *
     * @return {@code true} if this localization is in staging mode, {@code false} otherwise.
     */
    boolean isStaging();

    /**
     * Gets the localization version number.
     *
     * @return The localization version number.
     */
    String getVersion();

    /**
     * Gets the culture of this localization (for example "en-US").
     *
     * @return The culture of this localization.
     */
    String getCulture();

    /**
     * Gets the Java {@code Locale} of this localization.
     *
     * @return The Java {@code Locale} of this localization.
     */
    Locale getLocale();

    /**
     * Gets the site localizations associated with this localization.
     *
     * @return The site localizations associated with this localization.
     */
    List<SiteLocalization> getSiteLocalizations();

    /**
     * Gets a configuration item.
     *
     * @param key The key of the configuration item.
     * @return The configuration item or {@code} if there is no configuration item with this key.
     */
    String getConfiguration(String key);

    /**
     * Gets a resource string.
     *
     * @param key The key of the resource string.
     * @return The resource string or {@code null} if there is no resource with this key.
     */
    String getResource(String key);

    /**
     * Gets the semantic schemas defined for this localization by schema ID.
     *
     * @return A {@code Map} in which the keys are schema IDs and the values are {@code SemanticSchema} objects.
     */
    Map<Long, SemanticSchema> getSemanticSchemas();

    /**
     * Gets the includes for the specified page type.
     *
     * @param pageTypeId The page type ID.
     * @return The includes for the specified page type.
     */
    List<String> getIncludes(String pageTypeId);

    /**
     * Localize an URL by prefixing it with the path of this localization.
     *
     * @param url The URL.
     * @return The URL prefixed by the path of this localization.
     */
    String localizePath(String url);
}
