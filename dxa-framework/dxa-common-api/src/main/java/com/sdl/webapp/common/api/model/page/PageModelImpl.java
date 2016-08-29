package com.sdl.webapp.common.api.model.page;

import com.sdl.webapp.common.api.model.PageModel;
import lombok.NoArgsConstructor;

/**
 * Class for backward compatibility.
 * @deprecated since creation, since 1.6, will be dropped in preference for {@link DefaultPageModel}
 */
@Deprecated
@NoArgsConstructor
public class PageModelImpl extends AbstractPageModelImpl {

    public PageModelImpl(PageModel pageModel) {
        super(pageModel);
    }
}