package com.sdl.webapp.common.api.localization;

import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>Localization interface.</p>
 * @dxa.publicApi
 */
public interface Localization {
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
     * Determines if the specified URL refers to static content in this localization.
     *
     * @return {@code true} if the specified URL refers to static content in this localization, {@code false} otherwise.
     * @param url a {@link java.lang.String} object.
     */
    boolean isStaticContent(String url);

    /**
     * Determines if the specified URL refers to static content within the webapp
     *
     * @return {@code true} if the specified URL refers to static content within the webapp, {@code false} otherwise.
     * @param url a {@link java.lang.String} object.
     */
    default boolean isNonPublishedAsset(String url) {
        return false;
    }

    /**
     * <p>isDefault.</p>
     *
     * @return {@code true} when this is the default localization, {@code false} otherwise.
     */
    boolean isDefault();

    /**
     * <p>isStaging.</p>
     *
     * @return {@code true} if this localization is in staging mode, {@code false} otherwise.
     */
    boolean isStaging();

    /**
     * <p>getVersion.</p>
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
     * <p>getLocale.</p>
     *
     * @return The Java {@link java.util.Locale} of this localization.
     */
    Locale getLocale();

    /**
     * <p>getSiteLocalizations.</p>
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

    /**
     * Gets the Data Formats from the configuration in the CMS
     *
     * @return The list of configured data formats
     */
    List<String> getDataFormats();

}
