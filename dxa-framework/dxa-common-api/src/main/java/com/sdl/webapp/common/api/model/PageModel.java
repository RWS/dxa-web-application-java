package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * <p>PageModel interface.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface PageModel extends ViewModel {

    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getId();

    /**
     * <p>setId.</p>
     *
     * @param Id a {@link java.lang.String} object.
     */
    void setId(String Id);

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getName();

    /**
     * <p>setName.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    void setName(String name);

    /**
     * <p>getTitle.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getTitle();

    /**
     * <p>setTitle.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    void setTitle(String name);

    /**
     * <p>getMeta.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, String> getMeta();

    /**
     * <p>setMeta.</p>
     *
     * @param pageMeta a {@link java.util.Map} object.
     */
    void setMeta(Map<String, String> pageMeta);

    /**
     * <p>getRegions.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.RegionModelSet} object.
     */
    RegionModelSet getRegions();

    /**
     * <p>setRegions.</p>
     *
     * @param regions a {@link com.sdl.webapp.common.api.model.RegionModelSet} object.
     */
    void setRegions(RegionModelSet regions);

    /**
     * <p>containsRegion.</p>
     *
     * @param regionName a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean containsRegion(String regionName);

    /**
     * <p>setMvcData.</p>
     *
     * @param pageMvcData a {@link com.sdl.webapp.common.api.model.MvcData} object.
     */
    void setMvcData(MvcData pageMvcData);

    /**
     * <p>setXpmMetadata.</p>
     *
     * @param xpmMetaData a {@link java.util.Map} object.
     */
    void setXpmMetadata(Map<String, Object> xpmMetaData);
}
