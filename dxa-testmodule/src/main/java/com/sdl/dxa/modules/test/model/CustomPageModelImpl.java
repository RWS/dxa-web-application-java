package com.sdl.dxa.modules.test.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.page.PageModelImpl;

/**
 * Created by Administrator on 28/09/2015.
 */
public class CustomPageModelImpl extends PageModelImpl {

    @SemanticProperty("s:headline")
    private String headline;
}
