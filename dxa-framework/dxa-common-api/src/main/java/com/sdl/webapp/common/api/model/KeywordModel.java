package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base class for View Models representing a Keyword in CM.
 * <p>
 * This class can be used as an alternative for class {@link Tag}; it provides direct access to the Keyword's Id, Title, Description and Key.
 * You can also create a subclass with additional properties in case your Keyword has custom metadata which you want to use in your View.
 * Regular semantic mapping can be used to map the Keyword's metadata fields to properties of your subclass.
 * </p>
 *
 * @dxa.publicApi
 * @see Tag
 * @since 1.7
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KeywordModel extends AbstractViewModel {

    @JsonIgnore
    private String id;

    @JsonIgnore
    private String title;

    @JsonIgnore
    private String description;

    @JsonIgnore
    private String key;

    @JsonIgnore
    private String taxonomyId;

    @Override
    public String getXpmMarkup(Localization localization) {
        return "";
    }
}
