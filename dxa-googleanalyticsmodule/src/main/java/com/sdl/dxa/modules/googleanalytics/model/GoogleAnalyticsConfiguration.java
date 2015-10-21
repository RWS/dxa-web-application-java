package com.sdl.dxa.modules.googleanalytics.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
