package com.sdl.webapp.common.api.model;

import org.junit.Test;
import org.springframework.util.StringUtils;

import static org.junit.Assert.*;

public class MvcDataImplTest {

    @Test
    public void shouldShouldCorrectlyParseDifferentTypeOfInitNames() {
        //given
        String fullName = "Core:Entity:YouTubeVideo";
        String areaViewName = "Core:YouTubeVideo";
        String viewName = "YouTubeVideo";

        //when
        MvcDataImpl mvcDataFull = new MvcDataImpl(fullName);
        MvcDataImpl mvcDataHalf = new MvcDataImpl(areaViewName);
        MvcDataImpl mvcDataShort = new MvcDataImpl(viewName);

        //then
        assertPartsAreSet(mvcDataFull, new Parts()
                .controllerAreaName("Core")
                .controllerName("Entity")
                .areaName("Core")
                .viewName(viewName));
        assertPartsAreSet(mvcDataHalf, new Parts()
                .controllerAreaName("Core")
                .areaName("Core")
                .viewName(viewName));
        assertPartsAreSet(mvcDataShort, new Parts()
                .controllerAreaName("Core")
                .areaName("Core")
                .viewName(viewName));

        //when
        mvcDataFull.defaults(MvcDataImpl.Defaults.ENTITY);
        mvcDataHalf.defaults(MvcDataImpl.Defaults.ENTITY);
        mvcDataShort.defaults(MvcDataImpl.Defaults.ENTITY);

        //then
        Parts fullParts = new Parts()
                .controllerAreaName("Core")
                .controllerName("Entity")
                .actionName("Entity")
                .areaName("Core")
                .viewName(viewName);
        assertPartsAreSet(mvcDataFull, fullParts);
        assertPartsAreSet(mvcDataHalf, fullParts);
        assertPartsAreSet(mvcDataShort, fullParts);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForWrongInitNameWithTooManySemicolons() {
        //given
        String manySemicolons = "q:a:s:z";

        //when
        MvcDataImpl mvcData = new MvcDataImpl(manySemicolons);

        //then
//        exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForWrongInitNameWhichIsEmpty() {
        //given
        String empty = "";

        //when
        MvcDataImpl mvcData = new MvcDataImpl(empty);

        //then
//        exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForWrongInitNameWhichIsNull() {
        //given

        //when
        MvcDataImpl mvcData = new MvcDataImpl(null);

        //then
//        exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForWrongInitNamWithTooManySemicolons() {
        //given
        String manySemicolons = "q:a:s:z";

        //when
        MvcDataImpl mvcData = new MvcDataImpl(manySemicolons);

        //then
//        exception expected
    }

    @Test
    public void shouldSetDefaultsValuesForEntity() {
        //given
        String viewName = "YouTubeVideo";
        MvcDataImpl mvcData = new MvcDataImpl(viewName);

        //when
        mvcData.defaults(MvcDataImpl.Defaults.ENTITY);

        //then
        assertPartsAreSet(mvcData, new Parts()
                .controllerAreaName("Core")
                .controllerName("Entity")
                .actionName("Entity")
                .areaName("Core")
                .viewName(viewName));
    }

    @Test
    public void shouldBeConsistent() {
        //given
        MvcDataImpl mvcData = new MvcDataImpl("Core:Entity:YouTubeVideo");
        MvcDataImpl mvcData2 = new MvcDataImpl("Core:Entity:YouTubeVideo");

        //when

        //then
        assertEquals(mvcData, mvcData2);
        assertEquals(mvcData.hashCode(), mvcData2.hashCode());
    }

    private void assertPartsAreSet(MvcData mvcData, Parts parts) {
        assertEquals(parts.controllerAreaName, mvcData.getControllerAreaName());
        assertEquals(parts.controllerName, mvcData.getControllerName());
        assertEquals(parts.actionName, mvcData.getActionName());
        assertEquals(parts.areaName, mvcData.getAreaName());
        assertEquals(parts.viewName, mvcData.getViewName());
    }

    private class Parts {
        String controllerAreaName;
        String controllerName;
        String actionName;
        String areaName;
        String viewName;

        public Parts controllerAreaName(String controllerAreaName) {
            this.controllerAreaName = controllerAreaName;
            return this;
        }
        public Parts controllerName(String controllerName) {
            this.controllerName = controllerName;
            return this;
        }
        public Parts actionName(String actionName) {
            this.actionName = actionName;
            return this;
        }
        public Parts areaName(String areaName) {
            this.areaName = areaName;
            return this;
        }
        public Parts viewName(String viewName) {
            this.viewName = viewName;
            return this;
        }
    }

}