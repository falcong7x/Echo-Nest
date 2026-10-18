package com.econest.controller;

import com.econest.model.exception.DatabaseException;
import com.econest.model.exception.DuplicateRecordException;
import com.econest.model.exception.RecordNotFoundException;
import com.econest.model.exception.ValidationException;
import com.econest.model.GardeningKit;
import com.econest.model.Plant;
import com.econest.model.Planter;
import com.econest.model.Product;
import com.econest.model.service.ProductService;
import com.econest.view.ProductPanel;

/** Connects ProductPanel-view to ProductService-model. */
public class ProductController {

    private final ProductPanel view;
    private final ProductService service;

    public ProductController(ProductPanel view, ProductService service) {
        this.view = view;
        this.service = service;

        view.onAdd(e -> addProduct());
        view.onUpdate(e -> updateProduct());
        view.onDelete(e -> deleteProduct());
        view.onClear(e -> view.clearForm());
        view.onSearch(e -> search());
        view.onShowAll(e -> refresh());
        view.onRowSelected(this::loadSelected);
        refresh();
    }

    public void refresh() {
        try {
            view.setProducts(service.getAllProducts());
        } catch (DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void addProduct() {
        try {
            service.addProduct(buildFromForm(0));
            view.showMessage("Product added successfully.");
            view.clearForm();
            refresh();
        } catch (ValidationException | DuplicateRecordException | DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void updateProduct() {
        Product selected = view.getSelectedProduct();
        if (selected == null) {
            view.showError("Select a product in the table first.");
            return;
        }
        try {
            service.updateProduct(buildFromForm(selected.getId()));
            view.showMessage("Product updated successfully.");
            refresh();
        } catch (ValidationException | DuplicateRecordException | RecordNotFoundException
                 | DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void deleteProduct() {
        Product selected = view.getSelectedProduct();
        if (selected == null) {
            view.showError("Select a product in the table first.");
            return;
        }
        if (!view.confirm("Delete '" + selected.getName() + "'?")) {
            return;
        }
        try {
            service.deleteProduct(selected.getId());
            view.showMessage("Product deleted.");
            view.clearForm();
            refresh();
        } catch (RecordNotFoundException | DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void search() {
        try {
            view.setProducts(service.searchProducts(view.getSearchText()));
        } catch (DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void loadSelected() {
        Product selected = view.getSelectedProduct();
        if (selected != null) {
            view.populateForm(selected);
        }
    }

    /** Reads the form and builds the right subtype. Model setters validate the values. */
    private Product buildFromForm(int id) {
        String name = view.getNameInput();
        double price = parseNumber(view.getPriceInput(), "Price");
        int eco = view.getEcoInput();
        return switch (view.getSelectedType()) {
            case "Plant" -> new Plant(id, name, price, eco,
                    view.getDetail1(), view.getDetail2(), view.getDetail3());
            case "Planter" -> new Planter(id, name, price, eco, view.getDetail1(), view.getDetail2());
            default -> new GardeningKit(id, name, price, eco, view.getDetail1(),
                    parseNumber(view.getDetail2(), "Bundle discount"));
        };
    }

    private double parseNumber(String text, String field) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidationException(field + " must be a valid number.");
        }
    }
}
