package org.dd4t.test.models;

import org.dd4t.contentmodel.impl.KeywordImpl;
import org.dd4t.contentmodel.impl.MultimediaImpl;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

@ViewModel (rootElementNames = {"animal"} )
public class Animal extends TridionViewModelBase {
    @ViewModelProperty
    private String species;

    @ViewModelProperty
    private String speciesLatin;

    @ViewModelProperty
    private KeywordImpl classification;

    @ViewModelProperty
    private String description;

    @ViewModelProperty
    private MultimediaImpl image;

    @ViewModelProperty
    private KeywordImpl habitat;

    @ViewModelProperty
    private KeywordImpl distribution;

    @ViewModelProperty
    private KeywordImpl eats;

    public String getSpeciesLatin() {
        return speciesLatin;
    }
    public void setSpeciesLatin(String speciesLatin) {
        this.speciesLatin = speciesLatin;
    }
    public KeywordImpl getClassification() {
        return classification;
    }
    public void setClassification(KeywordImpl classification) {
        this.classification = classification;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public MultimediaImpl getImage() {
        return image;
    }
    public void setImage(MultimediaImpl image) {
        this.image = image;
    }
    public KeywordImpl getHabitat() {
        return habitat;
    }
    public void setHabitat(KeywordImpl habitat) {
        this.habitat = habitat;
    }
    public KeywordImpl getDistribution() {
        return distribution;
    }
    public void setDistribution(KeywordImpl distribution) {
        this.distribution = distribution;
    }
    public KeywordImpl getEats() {
        return eats;
    }
    public void setEats(KeywordImpl eats) {
        this.eats = eats;
    }
    public String getSpecies() {
        return species;
    }
    public void setSpecies(String species) {
        this.species = species;
    }
}
