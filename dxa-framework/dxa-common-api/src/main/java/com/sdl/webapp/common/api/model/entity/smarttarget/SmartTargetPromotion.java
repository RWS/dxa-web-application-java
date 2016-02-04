package com.sdl.webapp.common.api.model.entity.smarttarget;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * <p>SmartTargetPromotion class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SmartTargetPromotion extends AbstractEntityModel {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Slogan")
    private String slogan;

    @JsonProperty("Items")
    private List<SmartTargetItem> items;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXpmMarkup(Localization localization) {
        Map<String, Object> xpmMetadata = getXpmMetadata();
        if (xpmMetadata == null) {
            return "";
        }

        return String.format("<!-- Start Promotion: {{ \"PromotionID\": \"%s\", \"RegionID\" : \"%s\"}} -->",
                xpmMetadata.get("PromotionID"), xpmMetadata.get("RegionID"));
    }
}
