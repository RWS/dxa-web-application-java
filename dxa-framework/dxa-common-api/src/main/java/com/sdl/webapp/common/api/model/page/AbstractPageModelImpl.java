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
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Abstract implementation of page model. This is a basic extension point to create your page models.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    protected Map<String, Object> xpmMetadata = new HashMap<>();

    @JsonProperty("MvcData")
    protected MvcData mvcData;

    @JsonProperty("ExtensionData")
    private Map<String, Object> extensionData;

    /**
     * Default constructor for AbstractPageModelImpl.
     */
    public AbstractPageModelImpl() {
    }

    /**
     * Copy constructor for AbstractPageModelImpl.
     *
     * @param other a copy of other object
     */
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
    public void setXpmMetadata(Map<String, Object> xpmMetadata) {
        this.xpmMetadata = ImmutableMap.copyOf(xpmMetadata);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXpmMarkup(Localization localization) {
        String cmsUrl;
        if (!this.xpmMetadata.containsKey("CmsUrl")) {
            cmsUrl = localization.getConfiguration("core.cmsurl");
        } else {
            cmsUrl = String.valueOf(this.xpmMetadata.get("CmsUrl"));
        }
        if (cmsUrl.endsWith("/")) {
            // remove trailing slash from cmsUrl if present
            cmsUrl = cmsUrl.substring(0, cmsUrl.length() - 1);
        }

        return String.format(XPM_PAGE_SETTINGS_MARKUP,
                this.xpmMetadata.get("PageID"),
                this.xpmMetadata.get("PageModified"),
                this.xpmMetadata.get("PageTemplateID"),
                this.xpmMetadata.get("PageTemplateModified")
        ) + String.format(XPM_PAGE_SCRIPT, cmsUrl);
    }

    /**
     * Adds a key-value pair as an extension data.
     *
     * @param key   key for the value
     * @param value value to add
     */
    @Override
    public void addExtensionData(String key, Object value) {
        if (extensionData == null) {
            extensionData = new HashMap<>();
        }
        extensionData.put(key, value);
    }
}
