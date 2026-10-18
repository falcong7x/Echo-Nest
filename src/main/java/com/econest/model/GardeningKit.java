package com.econest.model;

import com.econest.model.exception.ValidationException;
import com.econest.model.util.Validator;

public class GardeningKit extends Product {

    private String contents;
    private double bundleDiscountPercent;

    public GardeningKit(int id, String name, double price, int ecoRating,
                        String contents, double bundleDiscountPercent) {
        super(id, name, price, ecoRating);
        setContents(contents);
        setBundleDiscountPercent(bundleDiscountPercent);
    }

    public String getContents() {
        return contents;
    }

    public final void setContents(String contents) {
        this.contents = Validator.requireText("Kit contents", contents);
    }

    public double getBundleDiscountPercent() {
        return bundleDiscountPercent;
    }

    public final void setBundleDiscountPercent(double percent) {
        if (Double.isNaN(percent) || percent < 0 || percent > 50) {
            throw new ValidationException("Bundle discount must be between 0 and 50 percent.");
        }
        this.bundleDiscountPercent = percent;
    }

    /** Polymorphism: a kit sells at its price minus the bundle discount. */
    @Override
    public double calculatePrice() {
        double discounted = getPrice() * (1 - bundleDiscountPercent / 100.0);
        return Math.round(discounted * 100.0) / 100.0;
    }

    @Override
    public String getCategory() {
        return "Gardening Kit";
    }

    @Override
    public String getDetails() {
        return contents + " | Bundle discount: " + bundleDiscountPercent + "%";
    }
}
