package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * <p>Location class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@SemanticEntities({
        @SemanticEntity(entityName = "GeoCoordinates", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
        @SemanticEntity(entityName = "LocationMeta", vocabulary = SDL_CORE, prefix = "lm")
})
public class Location extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("s:latitude"),
            @SemanticProperty("lm:latitude")
    })
    @JsonProperty("Latitude")
    private double latitude;

    @SemanticProperties({
            @SemanticProperty("s:longitude"),
            @SemanticProperty("lm:longitude")
    })
    @JsonProperty("Longitude")
    private double longitude;

    @SemanticProperty("lm:query")
    @JsonProperty("Query")
    private String query;

    /**
     * <p>Getter for the field <code>latitude</code>.</p>
     *
     * @return a double.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * <p>Setter for the field <code>latitude</code>.</p>
     *
     * @param latitude a double.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * <p>Getter for the field <code>longitude</code>.</p>
     *
     * @return a double.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * <p>Setter for the field <code>longitude</code>.</p>
     *
     * @param longitude a double.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * <p>Getter for the field <code>query</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getQuery() {
        return query;
    }

    /**
     * <p>Setter for the field <code>query</code>.</p>
     *
     * @param query a {@link java.lang.String} object.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", query='" + query + '\'' +
                '}';
    }
}
