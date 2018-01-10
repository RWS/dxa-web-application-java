package com.sdl.dxa.caching;

import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebRequestContextLocalizationIdProvider implements LocalizationIdProvider {

    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    public String getId() {
        return webRequestContext.getLocalization().getId();
    }
}
