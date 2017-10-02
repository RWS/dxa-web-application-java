package com.sdl.webapp.tridion.xpm.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.markup.MarkupDecorator;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.ParsableHtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * Entity XPM Markup
 *
 * @author nic
 */
public class EntityXpmMarkup implements MarkupDecorator {

    private static final String COMPONENT_PRESENTATION_PATTERN = "Start Component Presentation: " +
            "{\"ComponentID\":\"%s\",\"ComponentModified\":\"%s\",\"ComponentTemplateID\":\"%s\"," +
            "\"ComponentTemplateModified\":\"%s\",\"IsRepositoryPublished\":%b}";

    private static final String FIELD_PATTERN = "Start Component Field: {\"XPath\":\"%s\"}";

    /**
     * <p>processProperty.</p>
     *
     * @param propertyElement a {@link org.jsoup.nodes.Element} object.
     */
    protected static void processProperty(Element propertyElement) {

        String xpath = propertyElement.attr("data-entity-property-xpath");

        HtmlNode xpmMarkup = new HtmlCommentNode(String.format(FIELD_PATTERN, xpath));
        if (propertyElement.childNodes().size() > 0) {

            if (!propertyXpmMarkupAlreadyGenerated(propertyElement)) {
                propertyElement.prepend(xpmMarkup.toHtml());
            }
        } else {
            propertyElement.before(xpmMarkup.toHtml());
        }
        propertyElement.removeAttr("data-entity-property-xpath");
    }

    /**
     * <p>propertyXpmMarkupAlreadyGenerated.</p>
     *
     * @param propertyElement a {@link org.jsoup.nodes.Element} object.
     * @return a boolean.
     */
    protected static boolean propertyXpmMarkupAlreadyGenerated(Element propertyElement) {
        int index = 0;
        Node node = null;
        while (index < propertyElement.childNodes().size()) {
            node = propertyElement.childNode(index);
            if (!(node instanceof TextNode)) {
                break;
            }
            index++;
        }
        if (node != null && node instanceof Comment) {
            Comment comment = (Comment) node;
            if (comment.getData().contains("Start Component Field:")) {
                return true;
            }
        }
        return false;
    }

    private static HtmlNode buildXpmMarkup(EntityModel entity, Localization localization) {
        return new HtmlCommentNode(entity.getXpmMarkup(localization));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlNode process(HtmlNode markup, ViewModel model, WebRequestContext webRequestContext) {

        if (webRequestContext.isPreview()) {
            EntityModel entity = (EntityModel) model;

            boolean markupInjected = false;

            if (markup instanceof ParsableHtmlNode) {

                // Inject the XPM markup inside the entity markup
                //
                ParsableHtmlNode entityMarkup = (ParsableHtmlNode) markup;
                Element html = entityMarkup.getHtmlElement();
                if (html != null) {   // If an HTML element (not a comment etc)
                    html.prepend(buildXpmMarkup(entity, webRequestContext.getLocalization()).toHtml());
                    Elements properties = html.select("[data-entity-property-xpath]");
                    for (Element property : properties) {
                        processProperty(property);
                    }

                    markupInjected = true;
                }
            }

            if (!markupInjected) {
                // Surround the entity markup with the XPM markup
                //
                markup = HtmlBuilders.span()
                        .withNode(buildXpmMarkup(entity, webRequestContext.getLocalization()))
                        .withNode(markup).build();
            }
        }

        return markup;
    }

    @Override
    public int getOrder() {
        return 1;
    }

}
