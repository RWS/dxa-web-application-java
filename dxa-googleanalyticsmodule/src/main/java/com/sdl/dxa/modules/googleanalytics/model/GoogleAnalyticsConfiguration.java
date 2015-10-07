package com.sdl.dxa.modules.googleanalytics.model;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.entity.MediaItemMvcData;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.UUID;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "GoogleAnalyticsConfiguration", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
    public class GoogleAnalyticsConfiguration extends AbstractEntityModel {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsConfiguration.class);

    @SemanticProperty("siteKey")
    private String siteKey;

    public String getSiteKey() {
        return siteKey;
    }
}
