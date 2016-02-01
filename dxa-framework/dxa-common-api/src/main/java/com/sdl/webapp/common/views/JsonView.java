package com.sdl.webapp.common.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class JsonView extends MappingJackson2JsonView {

    @Autowired
    private WebRequestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // If manipulation of the page is needed it can be performed here
        setModelKey("data");
        super.renderMergedOutputModel(model, request, response);
    }
}
