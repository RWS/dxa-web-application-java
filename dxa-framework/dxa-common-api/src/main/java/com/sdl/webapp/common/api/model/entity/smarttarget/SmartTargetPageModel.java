package com.sdl.webapp.common.api.model.entity.smarttarget;

import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.page.AbstractPageModelImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@ToString
public class SmartTargetPageModel extends AbstractPageModelImpl {

    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean allowDuplicates;

    public SmartTargetPageModel() {
    }

    public SmartTargetPageModel(PageModel other) {
        super(other);
    }
}