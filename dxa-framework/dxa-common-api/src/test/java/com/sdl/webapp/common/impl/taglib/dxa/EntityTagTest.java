package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntityTagTest {

    @Test
    public void shouldReplaceViewNameIfItIsSet() throws Exception {
        //given
        EntityModel entity = new AbstractEntityModel() {
        };
        entity.setMvcData(MvcDataCreator.creator().create());
        EntityTag tag = new EntityTag();
        tag.setEntity(entity);
        tag.applyNewViewNameIfNeeded("TestArea:TestView");

        //when
        tag.applyNewViewNameIfNeeded();

        //then
        assertEquals("TestView", entity.getMvcData().getViewName());
    }
}