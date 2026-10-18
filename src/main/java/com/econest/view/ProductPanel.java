package com.econest.view;

import com.econest.model.GardeningKit;
import com.econest.model.Plant;
import com.econest.model.Planter;
import com.econest.model.Product;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;

/** View only: no business logic, no SQL. The controller wires the listeners. */
public class ProductPanel extends JPanel {

    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Plant", "Planter", "Gardening Kit"});
    private final JTextField nameField = new JTextField(18);
    private final JTextField priceField = new JTextField(10);
    private final JComboBox<Integer> ecoBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
    private final JLabel label1 = new JLabel();
    private final JLabel label2 = new JLabel();
    private final JLabel label3 = new JLabel();
    private final JTextField detail1 = new JTextField(18);
    private final JTextField detail2 = new JTextField(18);
    private final JTextField detail3 = new JTextField(18);

    private final JButton addButton = UiUtils.button("Add", UiUtils.PRIMARY);
    private final JButton updateButton = UiUtils.button("Update", UiUtils.PRIMARY);
    private final JButton deleteButton = UiUtils.button("Delete", UiUtils.DANGER);
    private final JButton clearButton = UiUtils.button("Clear", UiUtils.NEUTRAL);

    private final JTextField searchField = new JTextField(20);
    private final JButton searchButton = UiUtils.button("Search", UiUtils.NEUTRAL);
    private final JButton showAllButton = UiUtils.button("Show All", UiUtils.NEUTRAL);

    private final ProductTableModel tableModel = new ProductTableModel();
    private final JTable table = new JTable(tableModel);

    public ProductPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        typeBox.addActionListener(e -> updateDetailLabels());
        updateDetailLabels();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Product details"));
        place(form, new JLabel("Category"), 0, 0);
        place(form, typeBox, 1, 0);
        place(form, new JLabel("Name"), 2, 0);
        place(form, nameField, 3, 0);
        place(form, new JLabel("Price"), 0, 1);
        place(form, priceField, 1, 1);
        place(form, new JLabel("Eco-rating (1-5)"), 2, 1);
        place(form, ecoBox, 3, 1);
        place(form, label1, 0, 2);
        place(form, detail1, 1, 2);
        place(form, label2, 2, 2);
        place(form, detail2, 3, 2);
        place(form, label3, 0, 3);
        place(form, detail3, 1, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildTablePanel() {
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Search:"));
        searchBar.add(searchField);
        searchBar.add(searchButton);
        searchBar.add(showAllButton);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void place(JPanel p, Component c, int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        p.add(c, g);
    }

    private void updateDetailLabels() {
        switch (getSelectedType()) {
            case "Plant" -> setLabels("Species", "Sunlight needs", "Watering frequency");
            case "Planter" -> setLabels("Material", "Size", null);
            default -> setLabels("Kit contents", "Bundle discount (%)", null);
        }
    }

    private void setLabels(String l1, String l2, String l3) {
        label1.setText(l1);
        label2.setText(l2);
        label3.setVisible(l3 != null);
        detail3.setVisible(l3 != null);
        if (l3 != null) {
            label3.setText(l3);
        }
    }

    // ---- data in / out ----

    public String getSelectedType() {
        return (String) typeBox.getSelectedItem();
    }

    public String getNameInput() {
        return nameField.getText();
    }

    public String getPriceInput() {
        return priceField.getText();
    }

    public int getEcoInput() {
        return (Integer) ecoBox.getSelectedItem();
    }

    public String getDetail1() {
        return detail1.getText();
    }

    public String getDetail2() {
        return detail2.getText();
    }

    public String getDetail3() {
        return detail3.getText();
    }

    public String getSearchText() {
        return searchField.getText();
    }

    public void setProducts(List<Product> products) {
        tableModel.setProducts(products);
    }

    public Product getSelectedProduct() {
        int row = table.getSelectedRow();
        return row < 0 ? null : tableModel.getProductAt(row);
    }

    public void populateForm(Product p) {
        typeBox.setSelectedItem(p.getCategory());
        nameField.setText(p.getName());
        priceField.setText(String.valueOf(p.getPrice()));
        ecoBox.setSelectedItem(p.getEcoRating());
        if (p instanceof Plant pl) {
            detail1.setText(pl.getSpecies());
            detail2.setText(pl.getSunlightNeeds());
            detail3.setText(pl.getWateringFrequency());
        } else if (p instanceof Planter pt) {
            detail1.setText(pt.getMaterial());
            detail2.setText(pt.getSize());
            detail3.setText("");
        } else if (p instanceof GardeningKit k) {
            detail1.setText(k.getContents());
            detail2.setText(String.valueOf(k.getBundleDiscountPercent()));
            detail3.setText("");
        }
    }

    public void clearForm() {
        nameField.setText("");
        priceField.setText("");
        ecoBox.setSelectedIndex(0);
        detail1.setText("");
        detail2.setText("");
        detail3.setText("");
        table.clearSelection();
    }

    // ---- listeners and dialogs ----

    public void onAdd(ActionListener l) {
        addButton.addActionListener(l);
    }

    public void onUpdate(ActionListener l) {
        updateButton.addActionListener(l);
    }

    public void onDelete(ActionListener l) {
        deleteButton.addActionListener(l);
    }

    public void onClear(ActionListener l) {
        clearButton.addActionListener(l);
    }

    public void onSearch(ActionListener l) {
        searchButton.addActionListener(l);
        searchField.addActionListener(l);
    }

    public void onShowAll(ActionListener l) {
        showAllButton.addActionListener(l);
    }

    public void onRowSelected(Runnable r) {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                r.run();
            }
        });
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "EcoNest", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Please confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }
}
