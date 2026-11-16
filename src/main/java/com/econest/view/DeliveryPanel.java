package com.econest.view;

import com.econest.model.AgentOption;
import com.econest.model.Delivery;
import com.econest.model.OrderOption;

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
import java.time.LocalDate;
import java.util.List;

/** Delivery UI. Like the other EcoNest tabs, this class contains no SQL/business logic. */
public class DeliveryPanel extends JPanel {

    private final JTextField deliveryIdField = new JTextField(10);
    private final JComboBox<OrderOption> orderBox = new JComboBox<>();
    private final JComboBox<AgentOption> agentBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(12);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{
            "Assigned", "Scheduled", "In Transit", "Delivered", "Failed", "Cancelled"
    });
    private final JTextField addressField = new JTextField(22);

    private final JButton assignButton = UiUtils.button("Assign", UiUtils.PRIMARY);
    private final JButton updateButton = UiUtils.button("Update", UiUtils.PRIMARY);
    private final JButton deleteButton = UiUtils.button("Delete", UiUtils.DANGER);
    private final JButton clearButton = UiUtils.button("Clear", UiUtils.NEUTRAL);

    private final JTextField searchField = new JTextField(12);
    private final JButton searchButton = UiUtils.button("Search ID", UiUtils.NEUTRAL);
    private final JButton refreshButton = UiUtils.button("Refresh", UiUtils.NEUTRAL);
    private final JLabel lookupMessage = new JLabel(" ");

    private final DeliveryTableModel tableModel = new DeliveryTableModel();
    private final JTable table = new JTable(tableModel);

    public DeliveryPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        deliveryIdField.setEditable(false);
        dateField.setText(LocalDate.now().toString());
        add(buildFormSection(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildFormSection() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Delivery assignment details"));

        place(form, new JLabel("Delivery ID"), 0, 0);
        place(form, deliveryIdField, 1, 0);
        place(form, new JLabel("Order"), 2, 0);
        place(form, orderBox, 3, 0);
        place(form, new JLabel("Delivery agent"), 0, 1);
        place(form, agentBox, 1, 1);
        place(form, new JLabel("Delivery date (YYYY-MM-DD)"), 2, 1);
        place(form, dateField, 3, 1);
        place(form, new JLabel("Delivery status"), 0, 2);
        place(form, statusBox, 1, 2);
        place(form, new JLabel("Delivery address"), 2, 2);
        place(form, addressField, 3, 2);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttons.add(assignButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 4;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(4, 6, 4, 6);
        form.add(buttons, c);

        JPanel wrapper = new JPanel(new BorderLayout(4, 4));
        wrapper.add(form, BorderLayout.CENTER);
        lookupMessage.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 4));
        wrapper.add(lookupMessage, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildTableSection() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JLabel heading = new JLabel("Delivery records");
        heading.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        footer.add(new JLabel("Delivery ID:"));
        footer.add(searchField);
        footer.add(searchButton);
        footer.add(refreshButton);
        return footer;
    }

    private void place(JPanel panel, Component component, int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = (x == 1 || x == 3) ? 1 : 0;
        panel.add(component, c);
    }

    // ---- values exposed to the controller ----

    public int getSelectedOrderId() {
        OrderOption selected = (OrderOption) orderBox.getSelectedItem();
        return selected == null ? 0 : selected.getOrderId();
    }

    public int getSelectedAgentId() {
        AgentOption selected = (AgentOption) agentBox.getSelectedItem();
        return selected == null ? 0 : selected.getAgentId();
    }

    public String getDateInput() { return dateField.getText().trim(); }
    public String getStatusInput() { return (String) statusBox.getSelectedItem(); }
    public String getAddressInput() { return addressField.getText(); }
    public String getSearchInput() { return searchField.getText().trim(); }

    public int getFormDeliveryId() {
        try {
            return Integer.parseInt(deliveryIdField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Delivery getSelectedDelivery() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        return tableModel.getDeliveryAt(modelRow);
    }

    public void setOrders(List<OrderOption> orders) {
        orderBox.removeAllItems();
        for (OrderOption order : orders) orderBox.addItem(order);
    }

    public void setAgents(List<AgentOption> agents) {
        agentBox.removeAllItems();
        for (AgentOption agent : agents) agentBox.addItem(agent);
    }

    public void setDeliveries(List<Delivery> deliveries) {
        tableModel.setDeliveries(deliveries);
    }

    public void setLookupMessage(String message) {
        lookupMessage.setText(message == null || message.isBlank() ? " " : message);
    }

    public void populateForm(Delivery delivery) {
        deliveryIdField.setText(String.valueOf(delivery.getDeliveryId()));
        selectOrder(delivery.getOrderId());
        selectAgent(delivery.getAgentId());
        dateField.setText(delivery.getDeliveryDate() == null ? "" : delivery.getDeliveryDate().toString());
        statusBox.setSelectedItem(delivery.getDeliveryStatus());
        addressField.setText(delivery.getDeliveryAddress());
    }

    public void selectDeliveryInTable(int deliveryId) {
        for (int modelRow = 0; modelRow < tableModel.getRowCount(); modelRow++) {
            if (tableModel.getDeliveryAt(modelRow).getDeliveryId() == deliveryId) {
                int viewRow = table.convertRowIndexToView(modelRow);
                table.setRowSelectionInterval(viewRow, viewRow);
                return;
            }
        }
    }

    public void clearForm() {
        deliveryIdField.setText("");
        if (orderBox.getItemCount() > 0) orderBox.setSelectedIndex(0);
        if (agentBox.getItemCount() > 0) agentBox.setSelectedIndex(0);
        dateField.setText(LocalDate.now().toString());
        statusBox.setSelectedItem("Assigned");
        addressField.setText("");
        searchField.setText("");
        table.clearSelection();
    }

    // ---- event hooks ----

    public void onAssign(ActionListener listener) { assignButton.addActionListener(listener); }
    public void onUpdate(ActionListener listener) { updateButton.addActionListener(listener); }
    public void onDelete(ActionListener listener) { deleteButton.addActionListener(listener); }
    public void onClear(ActionListener listener) { clearButton.addActionListener(listener); }
    public void onSearch(ActionListener listener) {
        searchButton.addActionListener(listener);
        searchField.addActionListener(listener);
    }
    public void onRefresh(ActionListener listener) { refreshButton.addActionListener(listener); }

    public void onRowSelected(Runnable action) {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) action.run();
        });
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "EcoNest - Delivery", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "EcoNest - Delivery", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm delivery action",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void selectOrder(int orderId) {
        for (int i = 0; i < orderBox.getItemCount(); i++) {
            if (orderBox.getItemAt(i).getOrderId() == orderId) {
                orderBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectAgent(int agentId) {
        for (int i = 0; i < agentBox.getItemCount(); i++) {
            if (agentBox.getItemAt(i).getAgentId() == agentId) {
                agentBox.setSelectedIndex(i);
                return;
            }
        }
    }
}
