package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "GeoCoordinates", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
        @SemanticEntity(entityName = "LocationMeta", vocabulary = SDL_CORE, prefix = "lm")
})
public class Location extends AbstractEntity {

    @SemanticProperties({
            @SemanticProperty("s:latitude"),
            @SemanticProperty("lm:latitude")
    })
    private double latitude;

    @SemanticProperties({
            @SemanticProperty("s:longitude"),
            @SemanticProperty("lm:longitude")
    })
    private double longitude;

    @SemanticProperty("lm:query")
    private String query;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", query='" + query + '\'' +
                '}';
    }
}
