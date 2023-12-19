package com.sdl.dxa.common.util;

import com.sdl.dxa.api.datamodel.model.MvcModelData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MvcUtilsTest {

    @Test
    public void shouldParseQualifiedNames() {
        //when
        MvcModelData view = MvcUtils.parseMvcQualifiedViewName("View");
        MvcModelData areaView = MvcUtils.parseMvcQualifiedViewName("Area:View");
        MvcModelData full = MvcUtils.parseMvcQualifiedViewName("Area:Controller:View");

        //then
        assertEquals("View", view.getViewName());

        assertEquals("View", areaView.getViewName());
        assertEquals("Area", areaView.getAreaName());

        assertEquals("View", full.getViewName());
        assertEquals("Area", full.getAreaName());
        assertEquals("Controller", full.getControllerName());
    }
}