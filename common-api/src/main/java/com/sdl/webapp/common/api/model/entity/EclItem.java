package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.markup.html.HtmlElement;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * ECL Item
 *
 * @author nic
 */
@SemanticEntity(entityName = "ExternalContentItem", vocabulary = SDL_CORE, prefix = "s")
public abstract class EclItem extends MediaItem {

    private String eclUri;
    private String displayTypeId;
    private String templateFragment;

    public String getEclUri() {
        return this.eclUri;
    }

    public void setEclUri(String eclUri) {
        this.eclUri = eclUri;
    }

    public String getDisplayTypeId() {
        return displayTypeId;
    }

    public void setDisplayTypeId(String displayTypeId) {
        this.displayTypeId = displayTypeId;
    }

    public String getTemplateFragment() {
        return templateFragment;
    }

    public void setTemplateFragment(String templateFragment) {
        this.templateFragment = templateFragment;
    }

    @Override
    public String toString() {
        return "EclItem{" +
                "eclUri='" + eclUri + '\'' +
                ", displayTypeId='" + displayTypeId + '\'' +
                ", templateFragment='" + templateFragment + '\'' +
                '}';
    }

    @Override
    public String toHtml(String widthFactor) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass,
                         int containerSize) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    @Override
    public String getXpmMarkup(Localization localization)
    {

        // todo replace TCM URI with ECL URI
        // todo is the commented implementation right?
        // return super.getXpmMarkup(localization).replace(String.format("ecl:17-mm-379-dist-file", localization.getId(), this.getId()), getEclUri());
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }
}
