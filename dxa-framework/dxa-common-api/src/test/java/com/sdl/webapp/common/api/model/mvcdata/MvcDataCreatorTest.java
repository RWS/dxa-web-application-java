package com.sdl.webapp.common.api.model.mvcdata;

import com.sdl.webapp.common.api.model.MvcData;
import org.junit.Test;

import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;
import static com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl.builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MvcDataCreatorTest {

    @Test
    public void shouldShouldCorrectlyParseDifferentTypeOfInitNames() {
        //given
        String fullName = "Core:Entity:YouTubeVideo";
        String areaViewName = "Core:YouTubeVideo";
        String viewName = "YouTubeVideo";

        //when
        MvcData mvcDataFull = creator().fromQualifiedName(fullName).create();
        MvcData mvcDataHalf = creator().fromQualifiedName(areaViewName).create();
        MvcData mvcDataShort = creator().fromQualifiedName(viewName).create();

        //then
        assertPartsAreSet(new Parts()
                        .controllerAreaName("Core")
                        .controllerName("Entity")
                        .areaName("Core")
                        .viewName(viewName),
                mvcDataFull);
        assertPartsAreSet(new Parts()
                        .controllerAreaName("Core")
                        .areaName("Core")
                        .viewName(viewName),
                mvcDataHalf);
        assertPartsAreSet(new Parts()
                        .controllerAreaName("Core")
                        .areaName("Core")
                        .viewName(viewName),
                mvcDataShort);

        //when
        mvcDataFull = creator(mvcDataFull).defaults(DefaultsMvcData.CORE_ENTITY).create();
        mvcDataHalf = creator(mvcDataHalf).defaults(DefaultsMvcData.CORE_ENTITY).create();
        mvcDataShort = creator(mvcDataShort).defaults(DefaultsMvcData.CORE_ENTITY).create();

        //then
        Parts fullParts = new Parts()
                .controllerAreaName("Core")
                .controllerName("Entity")
                .actionName("Entity")
                .areaName("Core")
                .viewName(viewName);
        assertPartsAreSet(fullParts, mvcDataFull);
        assertPartsAreSet(fullParts, mvcDataHalf);
        assertPartsAreSet(fullParts, mvcDataShort);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForWrongInitNameWithTooManySemicolons() {
        //given
        String manySemicolons = "q:a:s:z";

        //when
        creator().fromQualifiedName(manySemicolons);

        //then
//        exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForWrongInitNameWhichIsEmpty() {
        //given
        String empty = "";

        //when
        creator().fromQualifiedName(empty);

        //then
//        exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForWrongInitNameWhichIsNull() {
        //given

        //when
        creator().fromQualifiedName(null);

        //then
//        exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForWrongInitNamWithTooManySemicolons() {
        //given
        String manySemicolons = "q:a:s:z";

        //when
        creator().fromQualifiedName(manySemicolons);

        //then
//        exception expected
    }

    @Test
    public void shouldSetDefaultsValuesForEntity() {
        //given
        String viewName = "YouTubeVideo";
        MvcDataCreator creator = creator().fromQualifiedName(viewName);

        //when
        MvcData mvcData = creator.defaults(DefaultsMvcData.CORE_ENTITY).create();

        //then
        assertPartsAreSet(new Parts()
                .controllerAreaName("Core")
                .controllerName("Entity")
                .actionName("Entity")
                .areaName("Core")
                .viewName(viewName), mvcData);
    }

    @Test
    public void shouldBeConsistent() {
        //given
        MvcData mvcData = creator().fromQualifiedName("Core:Entity:YouTubeVideo").create();
        MvcData mvcData2 = creator().fromQualifiedName("Core:Entity:YouTubeVideo").create();

        //when

        //then
        assertEquals(mvcData, mvcData2);
        assertEquals(mvcData.hashCode(), mvcData2.hashCode());
    }

    @Test
    public void shouldBeNoDifferenceWhenToCallDefaults() {
        //given

        //when
        MvcData mvcData = MvcDataCreator.creator()
                .defaults(DefaultsMvcData.CORE_ENTITY)
                .builder()
                .actionName("Test")
                .controllerAreaName("Test2")
                .controllerName("Test3")
                .areaName("Test4")
                .build();

        MvcData mvcData1 = creator(builder()
                .actionName("Test")
                .controllerAreaName("Test2")
                .controllerName("Test3")
                .areaName("Test4")
        )
                .defaults(DefaultsMvcData.CORE_ENTITY)
                .create();

        MvcData mvcData2 = creator(new MvcDataImpl()
                .setActionName("Test")
                .setControllerAreaName("Test2")
                .setControllerName("Test3")
                .setAreaName("Test4")
        )
                .defaults(DefaultsMvcData.CORE_ENTITY)
                .create();

        //then
        assertEquals(mvcData, mvcData1);
        assertEquals(mvcData, mvcData2);
    }

    @Test
    public void shouldFillInDefaultValues() {
        //given

        //when
        MvcDataImpl mvcData = creator().builder().build();

        //then
        assertNotNull(mvcData.getMetadata());
        assertNotNull(mvcData.getRouteValues());
        assertNotNull(mvcData.getAreaName());
        assertNotNull(mvcData.getControllerAreaName());
    }

    private void assertPartsAreSet(Parts parts, MvcData mvcData) {
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