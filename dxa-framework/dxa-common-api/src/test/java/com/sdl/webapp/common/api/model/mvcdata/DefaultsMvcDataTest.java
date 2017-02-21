package com.sdl.webapp.common.api.model.mvcdata;

import org.junit.Test;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.ENTITY;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.PAGE;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.REGION;
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
        assertEquals("AreaName", ENTITY.getAreaName());
        assertEquals("Framework", ENTITY.getControllerAreaName());

        assertEquals("AreaName", PAGE.getAreaName());
        assertEquals("Framework", PAGE.getControllerAreaName());

        assertEquals("AreaName", REGION.getAreaName());
        assertEquals("Framework", REGION.getControllerAreaName());
    }
}