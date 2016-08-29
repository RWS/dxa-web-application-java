package com.sdl.webapp.common.api.model.page;

import com.sdl.webapp.common.api.model.PageModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Default implementation of {@link PageModel}. This is a basic extension point to create your page models.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DefaultPageModel extends AbstractPageModelImpl implements PageModel {

    public DefaultPageModel(PageModel pageModel) {
        super(pageModel);
    }
}
