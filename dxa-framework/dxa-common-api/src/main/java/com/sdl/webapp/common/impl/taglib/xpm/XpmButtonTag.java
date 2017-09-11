package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.markup.html.HtmlMultiNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import lombok.Setter;

import static com.sdl.webapp.common.util.XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY;
import static com.sdl.webapp.common.util.XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY;
import static com.sdl.webapp.common.util.XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY;

@Setter
public class XpmButtonTag extends XpmMarkupTag {
    private RegionModel region;

    private String cssClass;

    private boolean isInclude() {
        return this.region.getXpmMetadata().get(INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY) == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HtmlNode generateXpmMarkup() {

        if (isInclude()) {
            String title = "Go Back";
            String editUrl = "javascript:history.back()";
            return HtmlBuilders.div()
                    .withClass("xpm-button" + (cssClass == null ? "" : " " + cssClass))
                    .withNode(HtmlBuilders.a(editUrl)
                            .withClass("fa-stack fa-lg")
                            .withTitle(title)
                            .withNode(
                                    new HtmlMultiNode(
                                            HtmlBuilders.i().withClass("fa fa-square fa-stack-2x").build(),
                                            HtmlBuilders.i().withClass("fa fa-arrow-left fa-inverse fa-stack-1x").build())
                            )
                            .build())
                    .build();
        } else {
            String title = "Edit " + this.region.getXpmMetadata().get(INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY);
            String urlOfInclude = this.region.getXpmMetadata().get(INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY).toString();
            if (!urlOfInclude.matches("/?system/include.*")) {
                urlOfInclude = String.format("system/include/%s", urlOfInclude.startsWith("/") ? urlOfInclude.substring(1) : urlOfInclude);
            }
            String editUrl = ApplicationContextHolder.getContext().getBean(WebRequestContext.class)
                    .getLocalization().localizePath(urlOfInclude);
            return HtmlBuilders.div()
                    .withClass("xpm-button" + (cssClass == null ? "" : " " + cssClass))
                    .withNode(HtmlBuilders.a(editUrl)
                            .withClass("fa-stack fa-lg")
                            .withTitle(title)
                            .withNode(
                                    new HtmlMultiNode(
                                            HtmlBuilders.i().withClass("fa fa-square fa-stack-2x").build(),
                                            HtmlBuilders.i().withClass("fa fa-pencil fa-inverse fa-stack-1x").build())
                            )
                            .build())
                    .build();
        }
    }
}
