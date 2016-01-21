package com.sdl.webapp.common.api.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@Setter
@Getter
public abstract class AbstractPageModelImpl implements PageModel {

    private static final String XPM_PAGE_SETTINGS_MARKUP = "<!-- Page Settings: {\"PageID\":\"%s\",\"PageModified\":\"%s\",\"PageTemplateID\":\"%s\",\"PageTemplateModified\":\"%s\"} -->";
    private static final String XPM_PAGE_SCRIPT = "<script type=\"text/javascript\" language=\"javascript\" defer=\"defer\" src=\"%s/WebUI/Editors/SiteEdit/Views/Bootstrap/Bootstrap.aspx?mode=js\" id=\"tridion.siteedit\"></script>";

    @JsonProperty("Id")
    protected String id;

    @JsonIgnore
    protected String name;

    @JsonProperty("Title")
    protected String title;

    @JsonIgnore
    protected String htmlClasses;

    @JsonProperty("Meta")
    protected Map<String, String> meta = new HashMap<>();

    @JsonProperty("Regions")
    protected RegionModelSet regions = new RegionModelSetImpl();

    @JsonProperty("XpmMetadata")
    protected Map<String, String> xpmMetadata = new HashMap<>();

    @JsonProperty("MvcData")
    protected MvcData mvcData;

    public AbstractPageModelImpl() {
    }

    public AbstractPageModelImpl(PageModel other) {
        this.id = other.getId();
        this.name = other.getName();
        this.title = other.getTitle();
        this.htmlClasses = other.getHtmlClasses();
        this.mvcData = other.getMvcData();

        this.regions.addAll(other.getRegions());
        this.meta.putAll(other.getMeta());
        this.xpmMetadata.putAll(other.getXpmMetadata());
    }

    @Override
    public boolean containsRegion(String regionName) {
        return !isEmpty(getRegions()) && getRegions().containsName(regionName);
    }

    @Override
    public void setXpmMetadata(Map<String, String> xpmMetadata) {
        this.xpmMetadata = ImmutableMap.copyOf(xpmMetadata);
    }

    @Override
    public String getXpmMarkup(Localization localization) {
        if (getXpmMetadata() == null) {
            return "";
        }

        String cmsUrl;
        if (!getXpmMetadata().containsKey("CmsUrl")) {
            cmsUrl = localization.getConfiguration("core.cmsurl");
        } else {
            cmsUrl = getXpmMetadata().get("CmsUrl");
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
}
