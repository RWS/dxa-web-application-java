package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BaseControllerTest {

    protected static Validator EMPTY_VALIDATOR = new Validator() {
        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }

        @Override
        public void validate(Object target, Errors errors) {
        }
    };

    @Test
    public void shouldBindDataFromRequest() throws Exception {
        //given
        BaseController controller = spy(new BaseController() {
            @Override
            protected boolean modelBindingRequired(ViewModel model, HttpServletRequest httpServletRequest) {
                return true;
            }

            @Nullable
            @Override
            protected Validator dataBindValidator() {
                return EMPTY_VALIDATOR;
            }
        });

        MockHttpServletRequest request = new MockHttpServletRequest();
        String expected = "myValue";
        request.setParameter("myProperty", expected);

        //when
        MyModel model = (MyModel) controller.enrichModel(new MyModel(), request);

        //then
        verify(controller).modelBindingRequired(eq(model), eq(request));
        verify(controller).dataBindValidator();

        assertEquals(expected, model.getMyProperty());
        assertFalse(((BindingResult) request.getAttribute("dataBinding")).hasErrors());
    }

    @Test
    public void shouldSkipValidationIfThereIsNoValidator() throws Exception {
        //given
        BaseController controller = new BaseController() {
            @Override
            protected boolean modelBindingRequired(ViewModel model, HttpServletRequest httpServletRequest) {
                return true;
            }
        };

        MockHttpServletRequest request = new MockHttpServletRequest();
        //when
        controller.enrichModel(new MyModel(), request);

        //then
        assertNull(request.getAttribute("dataBinding"));
    }

    @Test
    public void shouldSkipDataBindingIfNotNeeded() throws Exception {
        //given
        BaseController controller = new BaseController() {
        };
        MyModel model = new MyModel();
        MockHttpServletRequest request = new MockHttpServletRequest();
        String expected = "myValue";
        request.setParameter("myProperty", expected);

        //when
        controller.enrichModel(model, request);

        //then
        assertNull(model.getMyProperty());
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class MyModel extends AbstractEntityModel {

        private String myProperty;
    }
}