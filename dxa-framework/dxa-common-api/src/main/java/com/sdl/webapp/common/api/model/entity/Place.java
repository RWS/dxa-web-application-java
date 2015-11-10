package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.RichText;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public RichText getAddress() {
        return address;
    }

    public void setAddress(RichText address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
