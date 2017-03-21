package com.sdl.webapp.common.api.model.mvcdata;

import com.sdl.webapp.common.api.model.MvcData;
import org.junit.Test;

import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;
import static com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl.newBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MvcDataCreatorTest {

    private static void assertPartsAreSet(Parts parts, MvcData mvcData) {
        assertEquals(parts.controllerAreaName, mvcData.getControllerAreaName());
        assertEquals(parts.controllerName, mvcData.getControllerName());
        assertEquals(parts.actionName, mvcData.getActionName());
        assertEquals(parts.areaName, mvcData.getAreaName());
        assertEquals(parts.viewName, mvcData.getViewName());
    }

    @Test
    public void shouldShouldCorrectlyParseDifferentTypeOfInitNames() {
        //given
        String fullName = "Core:Entity:YouTubeVideo";
        String areaViewName = "Hello:YouTubeVideo";
        String viewName = "YouTubeVideo";

        //when
        MvcData mvcDataFull = creator()
                .fromQualifiedName(fullName)
                .defaults(DefaultsMvcData.ENTITY)
                .create();
        MvcData mvcDataHalf = creator()
                .fromQualifiedName(areaViewName)
                .defaults(DefaultsMvcData.ENTITY)
                .create();
        MvcData mvcDataShort = creator()
                .fromQualifiedName(viewName)
                .defaults(DefaultsMvcData.ENTITY)
                .create();

        //then

        assertPartsAreSet(new Parts()
                .controllerAreaName("Framework")
                .controllerName("Entity")
                .areaName("Core")
                .actionName("Entity")
                .viewName(viewName), mvcDataFull);
        assertPartsAreSet(new Parts()
                .controllerAreaName("Framework")
                .controllerName("Entity")
                .areaName("Hello")
                .actionName("Entity")
                .viewName(viewName), mvcDataHalf);
        assertPartsAreSet(new Parts()
                .controllerAreaName("Framework")
                .controllerName("Entity")
                .areaName("AreaName")
                .actionName("Entity")
                .viewName(viewName), mvcDataShort);

        //when
        mvcDataFull = creator(mvcDataFull).defaults(DefaultsMvcData.ENTITY).create();
        mvcDataHalf = creator(mvcDataHalf).defaults(DefaultsMvcData.ENTITY).create();
        mvcDataShort = creator(mvcDataShort).defaults(DefaultsMvcData.ENTITY).create();

        //then
        assertPartsAreSet(new Parts()
                .controllerAreaName("Framework")
                .controllerName("Entity")
                .actionName("Entity")
                .areaName("Core")
                .viewName(viewName), mvcDataFull);
        assertPartsAreSet(new Parts()
                .controllerAreaName("Framework")
                .controllerName("Entity")
                .actionName("Entity")
                .areaName("Hello")
                .viewName(viewName), mvcDataHalf);
        assertPartsAreSet(new Parts()
                .controllerAreaName("Framework")
                .controllerName("Entity")
                .actionName("Entity")
                .areaName("AreaName")
                .viewName(viewName), mvcDataShort);
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

    @Test(expected = NullPointerException.class)
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
        MvcData mvcData = creator.defaults(DefaultsMvcData.ENTITY).create();

        //then
        assertPartsAreSet(new Parts()
                .controllerAreaName("Framework")
                .controllerName("Entity")
                .actionName("Entity")
                .areaName("AreaName")
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
                .defaults(DefaultsMvcData.ENTITY)
                .builder()
                .actionName("Test")
                .controllerAreaName("Test2")
                .areaName("Test4")
                .build();

        MvcData mvcData1 = creator(newBuilder()
                .actionName("Test")
                .controllerAreaName("Test2")
                .areaName("Test4")
        )
                .defaults(DefaultsMvcData.ENTITY)
                .create();

        MvcData mvcData2 = creator(new MvcDataImpl.MvcDataImplBuilder().build()
                .setActionName("Test")
                .setControllerAreaName("Test2")
                .setAreaName("Test4")
        )
                .defaults(DefaultsMvcData.ENTITY)
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
    }

    @Test
    public void shouldMergeMvcDataToCreator() {
        //given
        MvcDataCreator emptyCreator = MvcDataCreator.creator();

        MvcDataImpl mvcData = new MvcDataImpl.MvcDataImplBuilder().build()
                .setActionName("Test")
                .setControllerAreaName("Test2")
                .setControllerName("Test3")
                .setAreaName("Test4");

        //when
        emptyCreator.mergeIn(mvcData);

        //then
        assertEquals(mvcData, emptyCreator.create());
    }

    private static class Parts {
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