package com.sdl.webapp.common.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


public class JsonView extends MappingJackson2JsonView {

    WebRequestContext context;

    private ObjectMapper objectMapper;

    public JsonView(WebRequestContext context) {
        this.context = context;

        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        setObjectMapper(objectMapper);

        setExtractValueFromSingleKeyModel(true);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // If manipulation of the page is needed it can be performed here
        setModelKey("data");
        super.renderMergedOutputModel(model, request, response);
    }
}
