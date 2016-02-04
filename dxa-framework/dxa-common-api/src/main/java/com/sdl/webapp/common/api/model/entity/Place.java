package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.RichText;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * <p>Place class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@SemanticEntity(entityName = "Place", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class Place extends AbstractEntityModel {

    @JsonProperty("Name")
    private String name;

    @SemanticProperty("s:image")
    @JsonProperty("Image")
    private Image image;

    @SemanticProperty("s:address")
    @JsonProperty("Address")
    private RichText address;

    @SemanticProperty("s:telephone")
    @JsonProperty("Telephone")
    private String telephone;

    @SemanticProperty("s:faxNumber")
    @JsonProperty("FaxNumber")
    private String faxNumber;

    @SemanticProperty("s:email")
    @JsonProperty("Email")
    private String email;

    @SemanticProperty("s:geo")
    @JsonProperty("Location")
    private Location location;

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>image</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.Image} object.
     */
    public Image getImage() {
        return image;
    }

    /**
     * <p>Setter for the field <code>image</code>.</p>
     *
     * @param image a {@link com.sdl.webapp.common.api.model.entity.Image} object.
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * <p>Getter for the field <code>address</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.RichText} object.
     */
    public RichText getAddress() {
        return address;
    }

    /**
     * <p>Setter for the field <code>address</code>.</p>
     *
     * @param address a {@link com.sdl.webapp.common.api.model.RichText} object.
     */
    public void setAddress(RichText address) {
        this.address = address;
    }

    /**
     * <p>Getter for the field <code>telephone</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * <p>Setter for the field <code>telephone</code>.</p>
     *
     * @param telephone a {@link java.lang.String} object.
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * <p>Getter for the field <code>faxNumber</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFaxNumber() {
        return faxNumber;
    }

    /**
     * <p>Setter for the field <code>faxNumber</code>.</p>
     *
     * @param faxNumber a {@link java.lang.String} object.
     */
    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    /**
     * <p>Getter for the field <code>email</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEmail() {
        return email;
    }

    /**
     * <p>Setter for the field <code>email</code>.</p>
     *
     * @param email a {@link java.lang.String} object.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.Location} object.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link com.sdl.webapp.common.api.model.entity.Location} object.
     */
    public void setLocation(Location location) {
        this.location = location;
    }
}
