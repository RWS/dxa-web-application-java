package com.sdl.webapp.common.api.model.entity.smarttarget;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.page.AbstractPageModelImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>SmartTargetPageModel class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class SmartTargetPageModel extends AbstractPageModelImpl {

    @Getter
    @Setter
    @Accessors(chain = true)
    @JsonProperty("AllowDuplicates")
    private boolean allowDuplicates;

    /**
     * <p>Constructor for SmartTargetPageModel.</p>
     */
    public SmartTargetPageModel() {
    }

    /**
     * <p>Constructor for SmartTargetPageModel.</p>
     *
     * @param other a {@link com.sdl.webapp.common.api.model.PageModel} object.
     */
    public SmartTargetPageModel(PageModel other) {
        super(other);
    }
}
