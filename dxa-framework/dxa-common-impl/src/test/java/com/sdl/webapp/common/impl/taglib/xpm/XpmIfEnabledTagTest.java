package com.sdl.webapp.common.impl.taglib.xpm;

import org.junit.Test;

import javax.servlet.jsp.tagext.Tag;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class XpmIfEnabledTagTest {

    @Test
    public void shouldStopTagIfNotPreview() throws Exception {
        //given
        XpmIfEnabledTag tag = spy(new XpmIfEnabledTag());
        doReturn(false).when(tag).isPreview();

        //when
        int doStartTag = tag.doStartTag();

        //then
        assertEquals(Tag.SKIP_BODY, doStartTag);
    }

    @Test
    public void shouldProceedTagIfPreview() throws Exception {
        //given
        XpmIfEnabledTag tag = spy(new XpmIfEnabledTag());
        doReturn(true).when(tag).isPreview();

        //when
        int doStartTag = tag.doStartTag();

        //then
        assertEquals(Tag.EVAL_BODY_INCLUDE, doStartTag);
    }

}