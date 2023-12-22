package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.RedirectEntity;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityControllerTest {

    @Test
    public void shouldReturnRedirectViewInCaseOfRedirectEntity() throws Exception {
        //given 
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("_entity_", new Link());

        //when
        String viewName = new EntityController() {
            @Override
            protected ViewModel enrichModel(ViewModel model, HttpServletRequest httpServletRequest) throws Exception {
                return new RedirectEntity("path");
            }
        }.handleGetEntity(request, "id");

        //then
        assertEquals("RedirectView", viewName);
    }

}