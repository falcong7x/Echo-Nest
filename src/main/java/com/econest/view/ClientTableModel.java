package com.econest.view;

import com.econest.model.Client;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClientTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "Name", "Type", "Email", "Phone", "Address", "Details"};
    private List<Client> clients = new ArrayList<>();

    public void setClients(List<Client> clients) {
        this.clients = new ArrayList<>(clients);
        fireTableDataChanged();
    }

    public Client getClientAt(int row) {
        return clients.get(row);
    }

    @Override
    public int getRowCount() {
        return clients.size();
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
        Client c = clients.get(row);
        return switch (col) {
            case 0 -> c.getId();
            case 1 -> c.getName();
            case 2 -> c.getClientType();
            case 3 -> c.getEmail();
            case 4 -> c.getPhone();
            case 5 -> c.getAddress();
            default -> c.getDetails();
        };
    }
}
