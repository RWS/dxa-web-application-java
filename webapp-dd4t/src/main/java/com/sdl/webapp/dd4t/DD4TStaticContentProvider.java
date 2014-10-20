package com.sdl.webapp.dd4t;

import com.sdl.webapp.common.api.ContentProviderException;
import com.sdl.webapp.common.api.Localization;
import com.sdl.webapp.common.api.StaticContentItem;
import com.sdl.webapp.common.api.StaticContentProvider;
import org.springframework.stereotype.Component;

@Component
public class DD4TStaticContentProvider implements StaticContentProvider {

    @Override
    public StaticContentItem getStaticContent(String url, Localization localization) throws ContentProviderException {
        // TODO: Implement this method
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
