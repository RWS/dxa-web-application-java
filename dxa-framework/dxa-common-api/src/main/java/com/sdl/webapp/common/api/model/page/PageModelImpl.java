package com.sdl.webapp.common.api.model.page;

import com.sdl.webapp.common.api.model.PageModel;

/**
 * Implementation of {@code PageModel}.
 */
public class PageModelImpl extends AbstractPageModelImpl implements PageModel {

    @Override
    public String toString() {
        return "PageModelImpl{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", mvcData='" + mvcData + '\'' +
                '}';
    }
}
