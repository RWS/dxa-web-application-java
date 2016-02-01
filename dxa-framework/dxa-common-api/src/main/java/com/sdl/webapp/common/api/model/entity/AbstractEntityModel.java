package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RichTextFragment;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.Map;

@SemanticMappingIgnore
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractEntityModel implements EntityModel, RichTextFragment {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("XpmMetadata")
    private Map<String, String> xpmMetadata;

    @JsonProperty("XpmPropertyMetadata")
    private Map<String, String> xpmPropertyMetadata;

    @JsonProperty("MvcData")
    private MvcData mvcData;

    @JsonProperty("HtmlClasses")
    private String htmlClasses;

    @Override
    @SneakyThrows(JsonProcessingException.class)
    public String getXpmMarkup(Localization localization) {
        return this.xpmMetadata == null ? "" : String.format("<!-- Start Component Presentation: %s -->",
                ApplicationContextHolder.getContext().getBean(ObjectMapper.class).writeValueAsString(this.xpmMetadata));
    }

    @Override
    public HtmlElement toHtmlElement() throws DxaException {
        throw new UnsupportedOperationException(
                String.format("Direct rendering of View Model type '%s' to HTML is not supported." +
                                " Consider using View Model property of type RichText in combination with DxaRichText() in view code to avoid direct rendering to HTML." +
                                " Alternatively, override method %s.toHtmlElement().",
                        getClass().getName(), getClass().getName())
        );
    }
}
