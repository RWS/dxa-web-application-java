package com.sdl.webapp.tridion.xpm.markup;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.markup.MarkupDecorator;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.HtmlTextNode;
import com.sdl.webapp.common.markup.html.ParsableHtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Entity XPM Markup
 *
 * @author nic
 */
public class EntityXpmMarkup implements MarkupDecorator {

    private static final String COMPONENT_PRESENTATION_PATTERN = "Start Component Presentation: " +
            "{\"ComponentID\":\"%s\",\"ComponentModified\":\"%s\",\"ComponentTemplateID\":\"%s\"," +
            "\"ComponentTemplateModified\":\"%s\",\"IsRepositoryPublished\":%s}";

    private static final String FIELD_PATTERN = "Start Component Field: {\"XPath\":\"%s\"}";

    @Override
    public HtmlNode process(HtmlNode markup, ViewModel model, WebRequestContext webRequestContext) {

        if ( webRequestContext.isPreview() ) {
            Entity entity = (Entity) model;

            boolean markupInjected = false;

            if ( markup instanceof ParsableHtmlNode ) {

                // Inject the XPM markup inside the entity markup
                //
                ParsableHtmlNode entityMarkup = (ParsableHtmlNode) markup;
                Element html = entityMarkup.getHtmlElement();
                if ( html != null ) {   // If an HTML element (not a comment etc)
                    html.prepend(buildXpmMarkup(entity).toHtml());
                    Elements properties = html.select("[data-entity-property-xpath]");
                    for ( Element property : properties ) {
                        processProperty(property);
                    }

                    markupInjected = true;
                }
            }

            if ( !markupInjected )
            {
                // Surround the entity markup with the XPM markup
                //
                markup = HtmlBuilders.span()
                        .withContent(buildXpmMarkup(entity))
                        .withContent(markup).build();
            }
        }

        return markup;
    }

    protected void processProperty(Element propertyElement) {

        String xpath = propertyElement.attr("data-entity-property-xpath");

        HtmlNode xpmMarkup = new HtmlCommentNode(String.format(FIELD_PATTERN, xpath));
        if ( propertyElement.childNodes().size()  > 0 ) {

            if ( !propertyXpmMarkupAlreadyGenerated(propertyElement) ) {
                propertyElement.prepend(xpmMarkup.toHtml());
            }
        }
        else {
            propertyElement.before(xpmMarkup.toHtml());
        }
        propertyElement.removeAttr("data-entity-property-xpath");
    }

    protected boolean propertyXpmMarkupAlreadyGenerated(Element propertyElement) {
        int index = 0;
        Node node = null;
        while ( index < propertyElement.childNodes().size() ) {
            node = propertyElement.childNode(index);
            if ( !(node instanceof TextNode) ) {
                break;
            }
            index++;
        }
        if ( node != null  && node instanceof Comment) {
            Comment comment = (Comment)node;
            if ( comment.getData().contains("Start Component Field:") ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    private HtmlNode buildXpmMarkup(Entity entity) {
        final Map<String, String> entityData = entity.getEntityData();

        final String componentId = entityData.get("ComponentID");
        if (Strings.isNullOrEmpty(componentId)) {
            return new HtmlTextNode("");
        }

        final String componentModified = entityData.get("ComponentModified");
        final String templateId = entityData.get("ComponentTemplateID");
        final String templateModified = entityData.get("ComponentTemplateModified");

        final String isRepositoryPublished;
        if (templateId.equals("tcm:0-0-0")) {
            isRepositoryPublished = "true,\"IsQueryBased\":true";
        } else {
            isRepositoryPublished = "false";
        }

        return new HtmlCommentNode(String.format(COMPONENT_PRESENTATION_PATTERN,
                componentId, componentModified, templateId, templateModified, isRepositoryPublished));
    }
}
