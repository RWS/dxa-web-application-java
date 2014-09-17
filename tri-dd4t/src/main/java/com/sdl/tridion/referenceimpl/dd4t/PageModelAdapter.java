package com.sdl.tridion.referenceimpl.dd4t;

import com.sdl.tridion.referenceimpl.model.PageModel;
import org.dd4t.contentmodel.GenericPage;

/**
 * TODO: Documentation.
 */
final class PageModelAdapter implements PageModel {

    private final GenericPage page;

    public PageModelAdapter(GenericPage page) {
        if (page == null) {
            throw new NullPointerException("page must not be null");
        }

        this.page = page;
    }

    @Override
    public String getTitle() {
        return page.getTitle();
    }

    @Override
    public String getViewName() {
        // TODO: Add error checking
        return (String) page.getPageTemplate().getMetadata().get("view").getValues().get(0);
    }
}
