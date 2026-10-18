package com.econest.model;

import com.econest.model.exception.ValidationException;
import com.econest.model.util.Validator;

/** Base class of the product hierarchy. Encapsulation: private fields + validated setters. */
public abstract class Product {

    private int id;
    private String name;
    private double price;
    private int ecoRating;

    protected Product(int id, String name, double price, int ecoRating) {
        this.id = id;
        setName(name);
        setPrice(price);
        setEcoRating(ecoRating);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = Validator.requireText("Product name", name);
    }

    public double getPrice() {
        return price;
    }

    public final void setPrice(double price) {
        if (Double.isNaN(price) || price <= 0) {
            throw new ValidationException("Price must be greater than 0.");
        }
        this.price = price;
    }

    public int getEcoRating() {
        return ecoRating;
    }

    public final void setEcoRating(int ecoRating) {
        if (ecoRating < 1 || ecoRating > 5) {
            throw new ValidationException("Eco-rating must be between 1 and 5.");
        }
        this.ecoRating = ecoRating;
    }

    /** "Plant", "Planter" or "Gardening Kit". */
    public abstract String getCategory();

    /** Short description of the subtype-specific attributes. */
    public abstract String getDetails();

    /** Polymorphic selling price. Subtypes override to apply their own rules. */
    public double calculatePrice() {
        return price;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + getCategory() + ")";
    }
}
