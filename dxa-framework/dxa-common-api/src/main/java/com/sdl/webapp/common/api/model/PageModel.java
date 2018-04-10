package com.sdl.webapp.common.api.model;

import com.sdl.dxa.caching.VolatileModel;
import com.sdl.webapp.common.api.formatters.support.FeedItemsProvider;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Page Model is a basic interface for Page Model implementations in DXA.
 *
 * @dxa.publicApi
 */
public interface PageModel extends ViewModel, FeedItemsProvider, CanFilterEntities, VolatileModel {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getTitle();

    void setTitle(String name);

    String getUrl();

    void setUrl(String url);

    Map<String, String> getMeta();

    void setMeta(Map<String, String> pageMeta);

    RegionModelSet getRegions();

    void setRegions(RegionModelSet regions);

    boolean containsRegion(String regionName);

    void setXpmMetadata(Map<String, Object> xpmMetaData);

    PageModel deepCopy();

    /**
     * <p>Implementors of this interface may want to save some data in a servlet response.</p>
     * <p>It is a workaround that might be removed in a future in case the better solution is found. So preferably
     * the good idea is <i>not to use it.</i></p>
     *
     * @dxa.publicApi
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
