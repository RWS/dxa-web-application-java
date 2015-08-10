package com.sdl.webapp.tridion.xpm.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.markup.MarkupDecorator;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.ParsableHtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.tridion.xpm.ComponentType;
import com.sdl.webapp.tridion.xpm.XpmRegion;
import com.sdl.webapp.tridion.xpm.XpmRegionConfig;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * Region XPM Markup
 *
 * @author nic
 */
public class RegionXpmMarkup implements MarkupDecorator {

    private XpmRegionConfig xpmRegionConfig;

    private static final String REGION_PATTERN = "Start Region: {title:\"%s\",allowedComponentTypes:[%s]," + "minOccurs:%d%s}";
    private static final String COMPONENT_TYPE_PATTERN = "%s{schema:\"%s\",template:\"%s\"}";
    private static final String MAX_OCCURS_PATTERN = ",maxOccurs:%d";

    public RegionXpmMarkup(XpmRegionConfig xpmRegionConfig) {
        this.xpmRegionConfig = xpmRegionConfig;
    }

    @Override
    public HtmlNode process(HtmlNode markup, ViewModel model, WebRequestContext webRequestContext) {

        if ( webRequestContext.isPreview() ) {
            Region region = (Region) model;

            // TODO determine min occurs and max occurs for the region
            final int minOccurs = 0;
            final int maxOccurs = 0;

            final XpmRegion xpmRegion = this.xpmRegionConfig.getXpmRegion(region.getName(), webRequestContext.getLocalization());
            if (xpmRegion != null) {

                boolean markupInjected = false;

                if ( markup instanceof ParsableHtmlNode ) {

                    // Inject the region markup with the XPM markup
                    //
                    ParsableHtmlNode regionMarkup = (ParsableHtmlNode) markup;
                    Element html = regionMarkup.getHtmlElement();
                    if ( html != null && ! this.isFirstNodeXpmEntityXPMMarkup(html) ) {
                        html.prepend(buildXpmMarkup(region, xpmRegion, minOccurs, maxOccurs).toHtml());
                        markupInjected = true;
                    }
                }

                if ( !markupInjected ) {

                    // Surround the region with XPM markup
                    //
                    markup = HtmlBuilders.span()
                            .withContent(buildXpmMarkup(region, xpmRegion, minOccurs, maxOccurs))
                            .withContent(markup).build();
                }
            }
        }
        return markup;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    private boolean isFirstNodeXpmEntityXPMMarkup(Element html) {

        for (Node child : html.childNodes() ) {
            if ( child instanceof Element ) {
                return false;
            }
            else if ( child instanceof Comment ) {
                Comment comment = (Comment) child;
                if ( comment.getData().startsWith(" Start Component Presentation") ) {
                    return true;
                }
            }
        }
        return false;
    }

    private HtmlNode buildXpmMarkup(Region region, XpmRegion xpmRegion, int minOccurs, int maxOccurs) {
        String separator = "";
        boolean first = true;
        final StringBuilder sb = new StringBuilder();
        for (ComponentType componentType : xpmRegion.getComponentTypes()) {
            sb.append(String.format(COMPONENT_TYPE_PATTERN, separator, componentType.getSchemaId(),
                    componentType.getTemplateId()));
            if (first) {
                first = false;
                separator = ",";
            }
        }
        HtmlNode xpmMarkup = new HtmlCommentNode(String.format(REGION_PATTERN, region.getName(), sb.toString(), minOccurs,
                maxOccurs > 0 ? String.format(MAX_OCCURS_PATTERN, maxOccurs) : ""));
        return xpmMarkup;
    }
}
