package com.sdl.webapp.common.api.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class KeywordModelTest extends PojosTest {

    @Test
    public void shouldReturnEmptyStringAsXpmMarkup() {
        //given 

        //when
        String xpmMarkup = new KeywordModel().getXpmMarkup(null);

        //then
        assertEquals("", xpmMarkup);
    }

    @Override
    protected Class<?> getPojoClass() {
        return KeywordModel.class;
    }
}