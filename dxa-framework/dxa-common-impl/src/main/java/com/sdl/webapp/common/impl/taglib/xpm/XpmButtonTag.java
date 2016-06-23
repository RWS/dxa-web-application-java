package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.markup.html.HtmlMultiNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>XpmButtonTag class.</p>
 */
public class XpmButtonTag extends XpmMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(XpmButtonTag.class);

    private RegionModel region;
	private String cssClass;

    /**
     * <p>Setter for the field <code>region</code>.</p>
     *
     * @param region a {@link com.sdl.webapp.common.api.model.RegionModel} object.
     */
    public void setRegion(RegionModel region) {
        this.region = region;
    }

    /**
     * <p>Setter for the field <code>cssClass</code>.</p>
     *
     * @param cssClass a {@link string} object.
     */
    public void setCssClass(String cssClass) {
        if (!Strings.isNullOrEmpty(cssClass)) {
            this.cssClass = " " + cssClass;
        } else {
            this.cssClass = "";
        }
    }

    private boolean isInclude() {
        return this.region.getXpmMetadata().get(RegionModelImpl.INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY) == null;
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
                    .withClass("xpm-button" + cssClass)
                    .withAttribute("style", "z-index:1")
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
            String path = this.pageContext.getServletContext().getContextPath();
            String title = "Edit " + this.region.getXpmMetadata().get(RegionModelImpl.INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY);
            String editUrl = '/' + path + this.region.getXpmMetadata().get(RegionModelImpl.INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY);
            return HtmlBuilders.div()
                    .withClass("xpm-button")
                    .withAttribute("style", "z-index:1")
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
