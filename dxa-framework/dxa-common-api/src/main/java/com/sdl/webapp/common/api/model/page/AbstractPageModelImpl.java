package com.sdl.webapp.common.api.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Abstract implementation of page model. This is a basic extension point to create your page models.
 *
 * @deprecated since 1.6, extend {@link DefaultPageModel} instead, the whole class contents will be moved there
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
@NoArgsConstructor
public abstract class AbstractPageModelImpl extends AbstractViewModel implements PageModel {

    private static final String XPM_PAGE_SETTINGS_MARKUP = "<!-- Page Settings: {\"PageID\":\"%s\",\"PageModified\":\"%s\",\"PageTemplateID\":\"%s\",\"PageTemplateModified\":\"%s\"} -->";

    private static final String XPM_PAGE_SCRIPT = "<script type=\"text/javascript\" language=\"javascript\" defer=\"defer\" src=\"%s/WebUI/Editors/SiteEdit/Views/Bootstrap/Bootstrap.aspx?mode=js\" id=\"tridion.siteedit\"></script>";

    @JsonProperty("Id")
    protected String id;

    @JsonIgnore
    protected String name;

    @JsonProperty("Title")
    protected String title;

    @JsonProperty("Meta")
    protected Map<String, String> meta = new HashMap<>();

    @JsonProperty("Regions")
    protected RegionModelSet regions = new RegionModelSetImpl();

    public AbstractPageModelImpl(PageModel pageModel) {
        this.id = pageModel.getId();
        this.name = pageModel.getName();
        this.title = pageModel.getTitle();
        this.regions.addAll(pageModel.getRegions());
        this.meta.putAll(pageModel.getMeta());

        setHtmlClasses(pageModel.getHtmlClasses());
        setMvcData(pageModel.getMvcData());
        addXpmMetadata(pageModel.getXpmMetadata());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsRegion(String regionName) {
        return !isEmpty(this.regions) && this.regions.containsName(regionName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXpmMarkup(Localization localization) {
        String cmsUrl;
        if (!getXpmMetadata().containsKey("CmsUrl")) {
            cmsUrl = localization.getConfiguration("core.cmsurl");
        } else {
            cmsUrl = String.valueOf(getXpmMetadata().get("CmsUrl"));
        }
        if (cmsUrl.endsWith("/")) {
            // remove trailing slash from cmsUrl if present
            cmsUrl = cmsUrl.substring(0, cmsUrl.length() - 1);
        }

        return String.format(XPM_PAGE_SETTINGS_MARKUP,
                getXpmMetadata().get("PageID"),
                getXpmMetadata().get("PageModified"),
                getXpmMetadata().get("PageTemplateID"),
                getXpmMetadata().get("PageTemplateModified")
        ) + String.format(XPM_PAGE_SCRIPT, cmsUrl);
    }

    @Override
    public List<FeedItem> extractFeedItems() {
        return collectFeedItems(regions);
    }
}
