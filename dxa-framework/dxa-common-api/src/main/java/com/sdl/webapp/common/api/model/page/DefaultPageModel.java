package com.sdl.webapp.common.api.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sdl.dxa.caching.NeverCached;
import com.sdl.dxa.caching.VolatileModel;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * Default implementation of page model. This is a basic extension point to create your page models.
 * @dxa.publicApi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "title", "url", "meta", "regions" })
@NoArgsConstructor
@lombok.ToString
public class DefaultPageModel extends AbstractViewModel implements PageModel, VolatileModel {

    private static final String XPM_PAGE_SETTINGS_MARKUP = "<!-- Page Settings: {\"PageID\":\"%s\",\"PageModified\":\"%s\",\"PageTemplateID\":\"%s\",\"PageTemplateModified\":\"%s\"} -->";

    private static final String XPM_PAGE_SCRIPT = "<script type=\"text/javascript\" language=\"javascript\" defer=\"defer\" src=\"%s/WebUI/Editors/SiteEdit/Views/Bootstrap/Bootstrap.aspx?mode=js\" id=\"tridion.siteedit\"></script>";

    protected String id;

    @JsonIgnore
    //todo dxa2 do we need this field?
    protected String name;

    @JsonProperty("Title")
    protected String title;

    @JsonProperty("Url")
    protected String url;

    @JsonProperty("Meta")
    protected Map<String, String> meta = new HashMap<>();

    @JsonProperty("Regions")
    protected RegionModelSet regions = new RegionModelSetImpl();

    @JsonIgnore
    private boolean staticModel;

    public DefaultPageModel(DefaultPageModel other) {
        super(other);
        this.id = other.getId();
        this.name = other.getName();
        this.title = other.getTitle();
        this.url = other.getUrl();
        if (other.getMeta() != null) {
            this.meta.putAll(other.getMeta());
        }
        if (other.getRegions() != null) {
            this.regions = new RegionModelSetImpl();
            this.regions.addAll(other.getRegions());
        }
        this.staticModel = other.staticModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsRegion(String regionName) {
        return !isEmpty(this.regions) && this.regions.containsName(regionName);
    }

    @Override
    public PageModel deepCopy() {
        DefaultPageModel clone = (DefaultPageModel) super.deepCopy();
        clone.regions = new RegionModelSetImpl();
        for (RegionModel regionModel : regions) {
            clone.getRegions().add(regionModel.deepCopy());
        }

        if (meta != null) {
            clone.meta = new HashMap<>(meta);
        }
        return clone;
    }

    @Override
    public void filterConditionalEntities(Collection<ConditionalEntityEvaluator> evaluators) throws ContentProviderException {
        final AtomicReference<ContentProviderException> exception = new AtomicReference<>();
        this.regions.forEach(regionModel -> {
            try {
                regionModel.filterConditionalEntities(evaluators);
            } catch (ContentProviderException ex) {
                if (exception.get() == null) exception.set(ex);
            }
        });
        if (exception.get() != null) throw exception.get();
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

    @Override
    public boolean canBeCached() {
        //If this pagemodel, or any of the regions cannot be cached; return false.

        return !this.getClass().isAnnotationPresent(NeverCached.class)
                && regions.stream().allMatch(regionModel -> regionModel.canBeCached());
    }
}
