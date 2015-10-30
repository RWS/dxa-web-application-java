package com.sdl.webapp.common.views;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.DataFormatter;
import com.sdl.webapp.common.api.model.PageModel;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class JsonView extends MappingJackson2JsonView {

    WebRequestContext context;

    public JsonView(WebRequestContext context) {
        this.context = context;
        setPrettyPrint(false);
        setExtractValueFromSingleKeyModel(true);
        getObjectMapper().registerModule(new JodaModule());
        getObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // If manipulation of the page is needed it can be performed here
        setModelKey("data");
        super.renderMergedOutputModel(model, request, response);
    }
}
