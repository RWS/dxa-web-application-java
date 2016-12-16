package com.sdl.webapp.common.api.model.mvcdata;

import org.junit.Test;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CORE_ENTITY;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CORE_PAGE;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CORE_REGION;
import static org.junit.Assert.assertEquals;

public class DefaultsMvcDataTest {

    @Test
    public void shouldReturnDefaultsFromConfiguration() {
        assertEquals("AreaName", DefaultsMvcData.getDefaultAreaName());
        assertEquals("RegionName", DefaultsMvcData.getDefaultRegionName());
        assertEquals("ControllerName", DefaultsMvcData.getDefaultControllerName());
        assertEquals("ControllerAreaName", DefaultsMvcData.getDefaultControllerAreaName());
        assertEquals("ActionName", DefaultsMvcData.getDefaultActionName());
    }

    @Test
    public void shouldSetDefaultForCoreEnums() {
        assertEquals("AreaName", CORE_ENTITY.getAreaName());
        assertEquals("Framework", CORE_ENTITY.getControllerAreaName());

        assertEquals("AreaName", CORE_PAGE.getAreaName());
        assertEquals("Framework", CORE_PAGE.getControllerAreaName());

        assertEquals("AreaName", CORE_REGION.getAreaName());
        assertEquals("Framework", CORE_REGION.getControllerAreaName());
    }
}