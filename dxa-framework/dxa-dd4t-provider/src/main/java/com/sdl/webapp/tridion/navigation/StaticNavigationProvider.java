package com.sdl.webapp.tridion.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.util.LocalizationUtils;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@Profile("!r2.provider")
public class StaticNavigationProvider extends AbstractStaticNavigationProvider {

    private static final Object $LOCK = new Object();

    private final PageFactory pageFactory;

    @Autowired
    public StaticNavigationProvider(ObjectMapper objectMapper, LinkResolver linkResolver, PageFactory pageFactory) {
        super(objectMapper, linkResolver);
        this.pageFactory = pageFactory;
    }

    @Override
    protected InputStream getPageContent(String path, Localization localization) throws ContentProviderException {
        return LocalizationUtils.findPageByPath(path, localization, (path1, publicationId) -> {
            final String pageContent;
            try {
                synchronized ($LOCK) {
                    pageContent = pageFactory.findSourcePageByUrl(path1, publicationId);
                }
            } catch (ItemNotFoundException e) {
                log.debug("Page not found: [{}] {}", publicationId, path1, e);
                return null;
            } catch (FactoryException e) {
                throw new ContentProviderException("Exception while getting page content for: [" + publicationId +
                        "] " + path1, e);
            }

            // NOTE: This assumes page content is always in UTF-8 encoding
            return new ByteArrayInputStream(pageContent.getBytes(StandardCharsets.UTF_8));
        });
    }
}
