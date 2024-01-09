package com.sdl.webapp.common.api.model.validation;

import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DynamicCodeResolverTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldReturnNullIfCodeIsNull() {
        //when
        String code = DynamicCodeResolver.resolveCode(null, new Link());

        //then
        assertNull(code);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldReturnNullIfModelIsNull() {
        //when
        String code = DynamicCodeResolver.resolveCode("code", null);

        //then
        assertNull(code);
    }

    @Test
    public void shouldResolveMessageByCode() {
        //when
        String message = DynamicCodeResolver.resolveCode("test", new TestEntity());

        //then
        assertEquals("Hello", message);
    }

    @Test
    public void shouldReturnNullIfNoAnnotationFound() {
        //given 

        //when
        String message = DynamicCodeResolver.resolveCode("test", new Link());

        //then
        assertNull(message);
    }

    private class TestEntity extends AbstractEntityModel {

        @DynamicValidationMessage(errorCode = "test")
        public String test() {
            return "Hello";
        }
    }
}