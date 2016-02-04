package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import org.springframework.core.Ordered;

/**
 * <p>PageBuilder interface.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface PageBuilder extends Ordered {
    /**
     * <p>createPage.</p>
     *
     * @param genericPage     a {@link org.dd4t.contentmodel.Page} object.
     * @param pageModel       a {@link com.sdl.webapp.common.api.model.PageModel} object.
     * @param localization    a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @param contentProvider a {@link com.sdl.webapp.common.api.content.ContentProvider} object.
     * @return a {@link com.sdl.webapp.common.api.model.PageModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    PageModel createPage(org.dd4t.contentmodel.Page genericPage, PageModel pageModel, Localization localization,
                         ContentProvider contentProvider) throws ContentProviderException;
}
