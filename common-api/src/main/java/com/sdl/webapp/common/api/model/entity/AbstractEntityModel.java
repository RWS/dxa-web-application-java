package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.mapping.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RichTextFragment;
import com.sdl.webapp.common.markup.html.HtmlElement;

import java.util.Map;

/**
 * Abstract superclass for implementations of {@code Entity}.
 */
@SemanticMappingIgnore
public abstract class AbstractEntityModel implements EntityModel, RichTextFragment {

    @JsonProperty("Id")
    private String id;

    @JsonIgnore
    private Map<String, String> xpmMetadata;

    @JsonIgnore
    private Map<String, String> xpmPropertyMetadata;

    @JsonIgnore
    private MvcData mvcData;

    @JsonIgnore
    private String htmlClasses;

    
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Map<String, String> getXpmMetadata() {
        return xpmMetadata;
    }

    public void setXpmMetadata(Map<String, String> xpmMetadata) {
        this.xpmMetadata = ImmutableMap.copyOf(xpmMetadata);
    }

    @Override
    public Map<String, String> getXpmPropertyMetadata() {
        return xpmPropertyMetadata;
    }

    public void setXpmPropertyMetadata(Map<String, String> propertyData) {
        this.xpmPropertyMetadata = ImmutableMap.copyOf(propertyData);
    }

    @Override
    public MvcData getMvcData() {
        return mvcData;
    }

    public void setMvcData(MvcData mvcData) {
        this.mvcData = mvcData;
    }
    
    @Override
    public String getHtmlClasses()
    {
    	return this.htmlClasses;
    }
    public void setHtmlClasses(String htmlClasses)
    {
    	this.htmlClasses = htmlClasses;
    }
    
    
    @Override
    public String toHtml()
    {
        throw new UnsupportedOperationException(
            String.format("Direct rendering of View Model type '%s' to HTML is not supported." + 
            " Consider using View Model property of type RichText in combination with DxaRichText() in view code to avoid direct rendering to HTML." +
            " Alternatively, override method %s.toHtml().", 
            getClass().getName())
            );
    }
}
