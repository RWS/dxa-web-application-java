package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;

public interface PageBuilder {
    PageModel createPage(org.dd4t.contentmodel.Page genericPage, PageModel pageModel, Localization localization, ContentProvider contentProvider) throws ContentProviderException;
}
