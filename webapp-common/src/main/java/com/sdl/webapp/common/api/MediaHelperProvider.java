package com.sdl.webapp.common.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MediaHelperProvider {

    private static final String CONTEXTUAL_MEDIA_HELPER = "contextualMediaHelper";

    @Autowired
    private ApplicationContext springContext;

    public MediaHelper getMediaHelper() {
        // Try to get contextualMediaHelper if it exists
        final Map<String, MediaHelper> map = springContext.getBeansOfType(MediaHelper.class);
        if (map.containsKey(CONTEXTUAL_MEDIA_HELPER)) {
            return map.get(CONTEXTUAL_MEDIA_HELPER);
        }

        // Otherwise it is expected that there is exactly one MediaHelper; get it
        return springContext.getBean(MediaHelper.class);
    }
}
