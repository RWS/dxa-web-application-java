package com.sdl.webapp.common.api.model.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.exceptions.DxaException;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "MediaObject", vocabulary = SCHEMA_ORG, prefix = "s")
public class Image extends MediaItem {

    @SemanticProperty("s:name")
    private String alternateText;

    @Autowired
    private MediaHelper mediaHelper;
    
    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    @Override
    public String toHtml(String widthFactor)
    {
    	return this.toHtml(widthFactor, 0,"", 0); 
    }
    
    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass, int containerSize)
    {
        String responsiveImageUrl = this.mediaHelper.getResponsiveImageUrl(getUrl(), widthFactor, aspect, containerSize);
        String dataAspect = String.valueOf((Math.round(aspect * 100) / 100));
        String widthAttr = Strings.isNullOrEmpty(widthFactor) ? null : String.format("width=\"{0}\"", widthFactor);
        String classAttr = Strings.isNullOrEmpty(cssClass) ? null : String.format("class=\"{0}\"", cssClass);
        return String.format("<img src=\"{0}\" alt=\"{1}\" data-aspect=\"{2}\" {3}{4}/>",
            responsiveImageUrl, getAlternateText(), dataAspect, widthAttr, classAttr);
    }
    
    @Override
    public  void readFromXhtmlElement(Node xhtmlElement)
    {
        super.readFromXhtmlElement(xhtmlElement);

        this.setAlternateText(xhtmlElement.getAttributes().getNamedItem("alt").getNodeValue());
        try {
    		this.setMvcData(new MediaItemMvcData("Core:Entity:Image"));
    	} catch (DxaException e) {

    	}
    }
    
    @Override
    public String toString() {
        return "Image{" +
                "url='" + getUrl() + '\'' +
                ", fileName='" + getFileName() + '\'' +
                ", fileSize=" + getFileSize() +
                ", mimeType='" + getMimeType() + '\'' +
                ", alternateText='" + alternateText + '\'' +
                '}';
    }
}
