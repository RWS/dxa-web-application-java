package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.springframework.util.StringUtils;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * ECL Item
 *
 * @author nic
 */
@SemanticEntity(entityName = "ExternalContentItem", vocabulary = SDL_CORE, prefix = "s")
public abstract class EclItem extends MediaItem {

    private String uri;
    private String displayTypeId;
    private String templateFragment;

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
    public String toHtml(String widthFactor) {
        // NOTE: params will be ignored
        return toHtml(widthFactor, 0.0, null, 0);
    }

    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass, int containerSize) {
        // NOTE: we're ignoring all parameters here.
        return templateFragment;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    @Override
    public String getXpmMarkup(Localization localization)
    {
        if (getXpmMetadata() != null && !StringUtils.isEmpty(this.uri)) {
            getXpmMetadata().put("ComponentID", this.uri);
        }
        return super.getXpmMarkup(localization);
    }

    @Override
    public String toString() {
        return "EclItem{" +
                "uri='" + uri + '\'' +
                ", displayTypeId='" + displayTypeId + '\'' +
                ", templateFragment='" + templateFragment + '\'' +
                '}';
    }
}
