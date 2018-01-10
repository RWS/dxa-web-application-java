package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.model.KeywordModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a Keyword in CM.
 * <p>
 * This class has hard-coded mappings to Keyword properties and does not support custom metadata on Keywords.
 * If this is too limiting for your implementation, use class {@link KeywordModel} instead.
 * Since there is no use in subclassing this class (unlike {@link KeywordModel}), it has been declared as sealed in DXA 1.7.
 * </p>
 *
 * @see KeywordModel
 * @dxa.publicApi
 */
@Data
@EqualsAndHashCode
public final class Tag {

    @JsonProperty("DisplayText")
    private String displayText;

    @JsonProperty("Key")
    private String key;

    @JsonProperty("TagCategory")
    private String tagCategory;
}
