package com.sdl.webapp.common.api.model.page;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;

import java.util.HashMap;
import java.util.Map;

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
