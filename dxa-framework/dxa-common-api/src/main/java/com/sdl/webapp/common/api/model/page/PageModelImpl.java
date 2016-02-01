package com.sdl.webapp.common.api.model.page;

import com.sdl.webapp.common.api.model.PageModel;
import lombok.ToString;

@ToString
public class PageModelImpl extends AbstractPageModelImpl implements PageModel {
    public PageModelImpl() {
    }

    public PageModelImpl(PageModel other) {
        super(other);
    }
}
