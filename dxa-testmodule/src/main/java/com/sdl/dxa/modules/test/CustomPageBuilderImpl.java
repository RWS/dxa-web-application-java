package com.sdl.dxa.modules.test;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.tridion.PageBuilder;
import org.dd4t.contentmodel.Page;
import org.springframework.stereotype.Component;

@Component
public class CustomPageBuilderImpl implements PageBuilder {
    @Override
    public PageModel createPage(Page genericPage, PageModel pageModel, Localization localization, ContentProvider contentProvider) throws ContentProviderException {
        pageModel.setTitle(pageModel.getTitle() + " - Custom processing done by CustomPageBuilder");
        return pageModel;
    }
}
