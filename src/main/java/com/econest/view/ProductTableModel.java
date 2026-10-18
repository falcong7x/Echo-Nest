package com.econest.view;

import com.econest.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "Name", "Category", "Price", "Selling Price", "Eco", "Details"};
    private List<Product> products = new ArrayList<>();

    public void setProducts(List<Product> products) {
        this.products = new ArrayList<>(products);
        fireTableDataChanged();
    }

    public Product getProductAt(int row) {
        return products.get(row);
    }

    @Override
    public int getRowCount() {
        return products.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Product p = products.get(row);
        return switch (col) {
            case 0 -> p.getId();
            case 1 -> p.getName();
            case 2 -> p.getCategory();
            case 3 -> String.format("%.2f", p.getPrice());
            case 4 -> String.format("%.2f", p.calculatePrice());
            case 5 -> p.getEcoRating() + "/5";
            default -> p.getDetails();
        };
    }
}
