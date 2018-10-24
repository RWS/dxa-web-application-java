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

    /**
     * <p>Constructor for RegionXpmMarkup.</p>
     *
     * @param xpmRegionConfig a {@link com.sdl.webapp.common.api.xpm.XpmRegionConfig} object.
     */
    public RegionXpmMarkup(XpmRegionConfig xpmRegionConfig) {
        this.xpmRegionConfig = xpmRegionConfig;
    }

    private static boolean isFirstNodeXpmEntityXPMMarkup(Element html) {

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

    private static HtmlNode buildXpmMarkup(RegionModel region, Localization localization) {
        return new HtmlCommentNode(region.getXpmMarkup(localization));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlNode process(HtmlNode markup, ViewModel model, WebRequestContext webRequestContext) {

        if (webRequestContext.isPreview()) {
            RegionModel region = (RegionModel) model;

            // TODO determine min occurs and max occurs for the region
            final int minOccurs = 0;
            final int maxOccurs = 0;

            String regionId;
            String schemaId = region.getSchemaId();
            if (schemaId != null && !schemaId.isEmpty()) {
                regionId = schemaId;
            } else {
                regionId = region.getName();
            }
            final XpmRegion xpmRegion = this.xpmRegionConfig.getXpmRegion(regionId, webRequestContext.getLocalization());
            if (xpmRegion != null) {

                boolean markupInjected = false;

                if (markup instanceof ParsableHtmlNode) {

                    // Inject the region markup with the XPM markup
                    //
                    ParsableHtmlNode regionMarkup = (ParsableHtmlNode) markup;
                    Element html = regionMarkup.getHtmlElement();
                    if (html != null && !RegionXpmMarkup.isFirstNodeXpmEntityXPMMarkup(html)) {
                        html.prepend(buildXpmMarkup(region, webRequestContext.getLocalization()).toHtml());
                        markupInjected = true;
                    }
                }

                if (!markupInjected) {

                    // Surround the region with XPM markup
                    //
                    markup = HtmlBuilders.span()
                            .withNode(buildXpmMarkup(region, webRequestContext.getLocalization()))
                            .withNode(markup).build();
                }
            }
        }
        return markup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
