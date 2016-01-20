package com.sdl.webapp.common.api.model.entity.smarttarget;

import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.page.AbstractPageModelImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString
public class SmartTargetPageModel extends AbstractPageModelImpl {

    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean allowDuplicates;

    public SmartTargetPageModel(PageModel pageModel) {
        setTitle(pageModel.getTitle());

        setMvcData(pageModel.getMvcData());
        setHtmlClasses(pageModel.getHtmlClasses());

        RegionModelSet regions = getRegions();
        regions.addAll(pageModel.getRegions());
        setRegions(regions);

        setXpmMetadata(pageModel.getXpmMetadata());

        Map<String, String> meta = getMeta();
        meta.putAll(pageModel.getMeta());
        setMeta(meta);
    }
}