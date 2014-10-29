package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "GeoCoordinates", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class Location extends AbstractEntity {

    @SemanticProperty("s:longitude")
    private double longitude;

    @SemanticProperty("s:latitude")
    private double latitude;

    private String query;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
