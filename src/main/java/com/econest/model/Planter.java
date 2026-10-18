package com.econest.model;

import com.econest.model.util.Validator;

public class Planter extends Product {

    private String material;
    private String size;

    public Planter(int id, String name, double price, int ecoRating, String material, String size) {
        super(id, name, price, ecoRating);
        setMaterial(material);
        setSize(size);
    }

    public String getMaterial() {
        return material;
    }

    public final void setMaterial(String material) {
        this.material = Validator.requireText("Material", material);
    }

    public String getSize() {
        return size;
    }

    public final void setSize(String size) {
        this.size = Validator.requireText("Size", size);
    }

    @Override
    public String getCategory() {
        return "Planter";
    }

    @Override
    public String getDetails() {
        return material + " | Size: " + size;
    }
}
