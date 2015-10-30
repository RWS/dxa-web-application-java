package com.sdl.webapp.tridion.xpm.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
import com.sdl.webapp.common.markup.MarkupDecorator;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.ParsableHtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * Region XPM Markup
 *
 * @author nic
 */
public class RegionXpmMarkup implements MarkupDecorator {

    private static final String REGION_PATTERN = "Start Region: {title:\"%s\",allowedComponentTypes:[%s]," + "minOccurs:%d%s}";
    private static final String COMPONENT_TYPE_PATTERN = "%s{schema:\"%s\",template:\"%s\"}";
    private static final String MAX_OCCURS_PATTERN = ",maxOccurs:%d";
    private XpmRegionConfig xpmRegionConfig;

    public RegionXpmMarkup(XpmRegionConfig xpmRegionConfig) {
        this.xpmRegionConfig = xpmRegionConfig;
    }

    @Override
    public HtmlNode process(HtmlNode markup, ViewModel model, WebRequestContext webRequestContext) {

        if (webRequestContext.isPreview()) {
            RegionModel region = (RegionModel) model;

            // TODO determine min occurs and max occurs for the region
            final int minOccurs = 0;
            final int maxOccurs = 0;

            final XpmRegion xpmRegion = this.xpmRegionConfig.getXpmRegion(region.getName(), webRequestContext.getLocalization());
            if (xpmRegion != null) {

                boolean markupInjected = false;

                if (markup instanceof ParsableHtmlNode) {

                    // Inject the region markup with the XPM markup
                    //
                    ParsableHtmlNode regionMarkup = (ParsableHtmlNode) markup;
                    Element html = regionMarkup.getHtmlElement();
                    if (html != null && !this.isFirstNodeXpmEntityXPMMarkup(html)) {
                        html.prepend(buildXpmMarkup(region, webRequestContext.getLocalization()).toHtml());
                        markupInjected = true;
                    }
                }

                if (!markupInjected) {

                    // Surround the region with XPM markup
                    //
                    markup = HtmlBuilders.span()
                            .withContent(buildXpmMarkup(region, webRequestContext.getLocalization()))
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

        for (Node child : html.childNodes()) {
            if (child instanceof Element) {
                return false;
            } else if (child instanceof Comment) {
                Comment comment = (Comment) child;
                if (comment.getData().startsWith(" Start Component Presentation")) {
                    return true;
                }
            }
        }
        return false;
    }

    private HtmlNode buildXpmMarkup(RegionModel region, Localization localization) {
        return new HtmlCommentNode(region.getXpmMarkup(localization));
    }
}
