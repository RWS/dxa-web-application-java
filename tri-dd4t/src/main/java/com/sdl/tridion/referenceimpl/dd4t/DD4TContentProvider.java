package com.sdl.tridion.referenceimpl.dd4t;

import com.sdl.tridion.referenceimpl.model.ContentProvider;
import com.sdl.tridion.referenceimpl.model.PageModel;
import com.sdl.tridion.referenceimpl.model.PageNotFoundException;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 */
@Component
public final class DD4TContentProvider implements ContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentProvider.class);

    // TODO: Publication id should be determined from configuration instead of being hard-coded
    private static final int PUBLICATION_ID = 48;

    @Autowired
    private PageFactory pageFactory;

    @Override
    public PageModel getPageModel(String uri) throws PageNotFoundException {
        LOG.debug("getPageModel: uri={}", uri);

        try {
            return new PageModelAdapter((GenericPage) pageFactory.findPageByUrl(uri, PUBLICATION_ID));
        } catch (ItemNotFoundException e) {
            throw new PageNotFoundException("Page not found: " + uri, e);
        }
    }
}
