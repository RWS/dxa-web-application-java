package com.sdl.dxa.tridion.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.modelservice.DefaultModelService;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.tridion.navigation.AbstractStaticNavigationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service("r2StaticNavigationProvider")
public class StaticNavigationProvider extends AbstractStaticNavigationProvider {

    private final DefaultModelService modelService;

    @Autowired
    public StaticNavigationProvider(ObjectMapper objectMapper, LinkResolver linkResolver, DefaultModelService modelService) {
        super(objectMapper, linkResolver);
        this.modelService = modelService;
    }

    @Override
    protected InputStream getPageContent(String path, Localization localization) throws ContentProviderException {
        String pageContent = modelService.loadPageContent(PageRequestDto.builder(localization.getId(), path).build());
        // NOTE: This assumes page content is always in UTF-8 encoding
        return new ByteArrayInputStream(pageContent.getBytes(StandardCharsets.UTF_8));
    }
}
