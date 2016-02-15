package com.sdl.webapp.common.controller;

/**
 * <p>RequestAttributeNames interface.</p>
 */
public interface RequestAttributeNames {

    /**
     * Constant <code>PAGE_MODEL="pageModel"</code>
     */
    String PAGE_MODEL = "pageModel"; // NOTE: Cannot be "page" because that interferes with the built-in "page" variable in JSPs
    /**
     * Constant <code>REGION_MODEL="region"</code>
     */
    String REGION_MODEL = "region";
    /** Constant <code>ENTITY_MODEL="entity"</code> */
    String ENTITY_MODEL = "entity";

    /** Constant <code>PAGE_ID="pageId"</code> */
    String PAGE_ID = "pageId";

    /** Constant <code>LOCALIZATION="localization"</code> */
    String LOCALIZATION = "localization";

    /** Constant <code>MARKUP="markup"</code> */
    String MARKUP = "markup";

    /** Constant <code>MEDIAHELPER="mediaHelper"</code> */
    String MEDIAHELPER = "mediaHelper";

    /** Constant <code>SCREEN_WIDTH="screenWidth"</code> */
    String SCREEN_WIDTH = "screenWidth";

    /** Constant <code>SOCIALSHARE_URL="socialshareUrl"</code> */
    String SOCIALSHARE_URL = "socialshareUrl";

    /** Constant <code>CONTEXTENGINE="contextengine"</code> */
    String CONTEXTENGINE = "contextengine";
}
