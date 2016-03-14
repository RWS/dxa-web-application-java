package com.sdl.webapp.common.api.model;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>PageModel interface.</p>
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

    /**
     * <p>Implementors of this interface may want to save some data in a servlet response.</p>
     * <p>It is a workaround that might be removed in a future in case the better solution is found. So preferably
     * the good idea if <i>not to use it.</i></p>
     */
    interface WithResponseData extends PageModel {
        /**
         * Passes the servlet response in order to allow page to modify it.
         *
         * @param servletResponse the current servlet response
         * @return true if the response was modified, false otherwise
         */
        boolean setResponseData(HttpServletResponse servletResponse);
    }
}
