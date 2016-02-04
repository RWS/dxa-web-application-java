package com.sdl.webapp.common.api.model.page;

import com.sdl.webapp.common.api.model.PageModel;
import lombok.ToString;

@ToString
/**
 * <p>PageModelImpl class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class PageModelImpl extends AbstractPageModelImpl implements PageModel {
    /**
     * <p>Constructor for PageModelImpl.</p>
     */
    public PageModelImpl() {
    }

    /**
     * <p>Constructor for PageModelImpl.</p>
     *
     * @param other a {@link com.sdl.webapp.common.api.model.PageModel} object.
     */
    public PageModelImpl(PageModel other) {
        super(other);
    }
}
