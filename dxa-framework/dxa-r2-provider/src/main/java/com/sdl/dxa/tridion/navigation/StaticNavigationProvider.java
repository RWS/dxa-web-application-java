package com.sdl.dxa.tridion.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.R2;
import com.sdl.dxa.tridion.rest.ModelService;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.tridion.navigation.AbstractStaticNavigationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@R2
@Service("r2StaticNavigationProvider")
public class StaticNavigationProvider extends AbstractStaticNavigationProvider {

    @Autowired
    private ModelService modelService;

    public StaticNavigationProvider(ObjectMapper objectMapper, LinkResolver linkResolver) {
        super(objectMapper, linkResolver);
    }

    @Override
    protected InputStream getPageContent(String path, Localization localization) throws ContentProviderException {
        String pageContent = modelService.loadPageSource(path);
        // NOTE: This assumes page content is always in UTF-8 encoding
        return new ByteArrayInputStream(pageContent.getBytes(StandardCharsets.UTF_8));
    }
}
