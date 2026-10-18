package com.econest.view;

import com.econest.model.Client;
import com.econest.model.HouseholdClient;
import com.econest.model.OfficeClient;

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
public class ClientPanel extends JPanel {

    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Household", "Office"});
    private final JTextField nameField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JTextField phoneField = new JTextField(14);
    private final JTextField addressField = new JTextField(18);
    private final JLabel label1 = new JLabel();
    private final JLabel label2 = new JLabel();
    private final JTextField detail1 = new JTextField(18);
    private final JTextField detail2 = new JTextField(18);

    private final JButton addButton = UiUtils.button("Add", UiUtils.PRIMARY);
    private final JButton updateButton = UiUtils.button("Update", UiUtils.PRIMARY);
    private final JButton deleteButton = UiUtils.button("Delete", UiUtils.DANGER);
    private final JButton clearButton = UiUtils.button("Clear", UiUtils.NEUTRAL);

    private final JTextField searchField = new JTextField(20);
    private final JButton searchButton = UiUtils.button("Search", UiUtils.NEUTRAL);
    private final JButton showAllButton = UiUtils.button("Show All", UiUtils.NEUTRAL);

    private final ClientTableModel tableModel = new ClientTableModel();
    private final JTable table = new JTable(tableModel);

    public ClientPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        typeBox.addActionListener(e -> updateDetailLabels());
        updateDetailLabels();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Client details"));
        place(form, new JLabel("Client type"), 0, 0);
        place(form, typeBox, 1, 0);
        place(form, new JLabel("Name"), 2, 0);
        place(form, nameField, 3, 0);
        place(form, new JLabel("Email"), 0, 1);
        place(form, emailField, 1, 1);
        place(form, new JLabel("Phone"), 2, 1);
        place(form, phoneField, 3, 1);
        place(form, new JLabel("Address"), 0, 2);
        place(form, addressField, 1, 2);
        place(form, label1, 2, 2);
        place(form, detail1, 3, 2);
        place(form, label2, 0, 3);
        place(form, detail2, 1, 3);

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
        boolean office = "Office".equals(getSelectedType());
        label1.setText(office ? "Company name" : "Household size");
        label2.setText("Contact person");
        label2.setVisible(office);
        detail2.setVisible(office);
    }

    // ---- data in / out ----

    public String getSelectedType() {
        return (String) typeBox.getSelectedItem();
    }

    public String getNameInput() {
        return nameField.getText();
    }

    public String getEmailInput() {
        return emailField.getText();
    }

    public String getPhoneInput() {
        return phoneField.getText();
    }

    public String getAddressInput() {
        return addressField.getText();
    }

    public String getDetail1() {
        return detail1.getText();
    }

    public String getDetail2() {
        return detail2.getText();
    }

    public String getSearchText() {
        return searchField.getText();
    }

    public void setClients(List<Client> clients) {
        tableModel.setClients(clients);
    }

    public Client getSelectedClient() {
        int row = table.getSelectedRow();
        return row < 0 ? null : tableModel.getClientAt(row);
    }

    public void populateForm(Client c) {
        typeBox.setSelectedItem(c.getClientType());
        nameField.setText(c.getName());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
        addressField.setText(c.getAddress());
        if (c instanceof HouseholdClient h) {
            detail1.setText(String.valueOf(h.getHouseholdSize()));
            detail2.setText("");
        } else if (c instanceof OfficeClient o) {
            detail1.setText(o.getCompanyName());
            detail2.setText(o.getContactPerson());
        }
    }

    public void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        detail1.setText("");
        detail2.setText("");
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
