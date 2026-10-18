package com.econest.model;

import com.econest.model.util.Validator;

public class Plant extends Product {

    private String species;
    private String sunlightNeeds;
    private String wateringFrequency;

    public Plant(int id, String name, double price, int ecoRating,
                 String species, String sunlightNeeds, String wateringFrequency) {
        super(id, name, price, ecoRating);
        setSpecies(species);
        setSunlightNeeds(sunlightNeeds);
        setWateringFrequency(wateringFrequency);
    }

    public String getSpecies() {
        return species;
    }

    public final void setSpecies(String species) {
        this.species = Validator.requireText("Species", species);
    }

    public String getSunlightNeeds() {
        return sunlightNeeds;
    }

    public final void setSunlightNeeds(String sunlightNeeds) {
        this.sunlightNeeds = Validator.requireText("Sunlight needs", sunlightNeeds);
    }

    public String getWateringFrequency() {
        return wateringFrequency;
    }

    public final void setWateringFrequency(String wateringFrequency) {
        this.wateringFrequency = Validator.requireText("Watering frequency", wateringFrequency);
    }

    @Override
    public String getCategory() {
        return "Plant";
    }

    @Override
    public String getDetails() {
        return species + " | Sun: " + sunlightNeeds + " | Water: " + wateringFrequency;
    }
}
