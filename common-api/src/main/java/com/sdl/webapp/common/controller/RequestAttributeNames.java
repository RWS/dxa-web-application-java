package com.sdl.webapp.common.controller;

public interface RequestAttributeNames {

    String PAGE_MODEL = "pageModel"; // NOTE: Cannot be "page" because that interferes with the built-in "page" variable in JSPs
    String REGION_MODEL = "region";
    String ENTITY_MODEL = "entity";

    String PAGE_ID = "pageId";

    String LOCALIZATION = "localization";

    String MARKUP = "markup";

    String MEDIAHELPER = "mediaHelper";

    String SCREEN_WIDTH = "screenWidth";

    String SOCIALSHARE_URL = "socialshareUrl";
}
