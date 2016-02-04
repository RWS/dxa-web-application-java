package com.sdl.webapp.common.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import org.joda.time.DateTime;

/**
 * <p>Markup interface.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface Markup {

    /**
     * <p>url.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String url(String path);

    /**
     * <p>versionedContent.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String versionedContent(String path);

    /**
     * <p>region.</p>
     *
     * @param region a {@link com.sdl.webapp.common.api.model.RegionModel} object.
     * @return a {@link java.lang.String} object.
     */
    String region(RegionModel region);

    /**
     * <p>entity.</p>
     *
     * @param entity a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @return a {@link java.lang.String} object.
     */
    String entity(EntityModel entity);

    /**
     * <p>property.</p>
     *
     * @param entity    a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String property(EntityModel entity, String fieldName);

    /**
     * <p>property.</p>
     *
     * @param entity    a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @param fieldName a {@link java.lang.String} object.
     * @param index     a int.
     * @return a {@link java.lang.String} object.
     */
    String property(EntityModel entity, String fieldName, int index);

    /**
     * <p>resource.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String resource(String key);

    /**
     * <p>formatDate.</p>
     *
     * @param dateTime a {@link org.joda.time.DateTime} object.
     * @return a {@link java.lang.String} object.
     */
    String formatDate(DateTime dateTime);

    /**
     * <p>formatDateDiff.</p>
     *
     * @param dateTime a {@link org.joda.time.DateTime} object.
     * @return a {@link java.lang.String} object.
     */
    String formatDateDiff(DateTime dateTime);

    /**
     * <p>formatMessage.</p>
     *
     * @param pattern a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    String formatMessage(String pattern, Object... args);

    /**
     * <p>replaceLineEndsWithHtmlBreaks.</p>
     *
     * @param text a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    String replaceLineEndsWithHtmlBreaks(String text);

    /**
     * <p>siteMapList.</p>
     *
     * @param item a {@link com.sdl.webapp.common.api.model.entity.SitemapItem} object.
     * @return a {@link java.lang.String} object.
     */
    String siteMapList(SitemapItem item);

    /**
     * <p>getWebRequestContext.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     */
    WebRequestContext getWebRequestContext();
}
