package com.sdl.webapp.main;

public interface RequestAttributeNames {

    String PAGE_MODEL = "pageModel"; // NOTE: Cannot be "page" because that interferes with the built-in "page" variable in JSPs
    String REGION_MODEL = "region";
    String ENTITY_MODEL = "entity";

    String LOCALIZATION = "localization";

    String MARKUP = "markup";

    String SCREEN_WIDTH = "screenWidth";
}
