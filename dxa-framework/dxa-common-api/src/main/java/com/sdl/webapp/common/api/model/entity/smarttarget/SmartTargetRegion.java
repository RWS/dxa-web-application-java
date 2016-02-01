package com.sdl.webapp.common.api.model.entity.smarttarget;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * <p>SmartTargetRegion class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SmartTargetRegion extends RegionModelImpl {
    /**
     * Indicates whether the Region has SmartTarget content (Promotions) or fallback content.
     */
    @JsonProperty("HasSmartTargetContent")
    private boolean isWithSmartTargetContent;

    /**
     * The maximum number of SmartTarget items to output in this Region.
     */
    @JsonProperty("MaxItems")
    private int maxItems;

    /**
     * <p>Constructor for SmartTargetRegion.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public SmartTargetRegion(String name) throws DxaException {
        super(name);
    }

    /**
     * <p>Constructor for SmartTargetRegion.</p>
     *
     * @param name              a {@link java.lang.String} object.
     * @param qualifiedViewName a {@link java.lang.String} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public SmartTargetRegion(String name, String qualifiedViewName) throws DxaException {
        super(name, qualifiedViewName);
    }

    /**
     * <p>Constructor for SmartTargetRegion.</p>
     *
     * @param mvcData a {@link com.sdl.webapp.common.api.model.MvcData} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public SmartTargetRegion(MvcData mvcData) throws DxaException {
        super(mvcData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXpmMarkup(Localization localization) {
        return String.format("<!-- Start Promotion Region: {{ \"RegionID\": \"%s\"}} -->", this.getName());
    }

    /**
     * Gets the Start Query XPM markup (for staging sites).
     * <p>
     * A SmartTarget Region has two pieces of XPM markup: a "Start Promotion Region" tag and a "Start Query" tag.
     * The regular XPM markup mechanism (Html.DxaRegionMarkup()) renders the "Start Promotion Region" tag and this method
     * should be called from the Region View code to render the "Start Query" tag in the right location.
     * </p>
     *
     * @return the "Start Query" XPM markup if the site is a staging site or an empty string otherwise
     */
    public String getStartQueryXpmMarkup() {
        Map<String, String> xpmMetadata = this.getXpmMetadata();
        return xpmMetadata != null && xpmMetadata.containsKey("Query") ? xpmMetadata.get("Query") : "";
    }
}
