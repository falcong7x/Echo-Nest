package com.econest.controller;

import com.econest.model.exception.DatabaseException;
import com.econest.model.exception.DuplicateRecordException;
import com.econest.model.exception.RecordNotFoundException;
import com.econest.model.exception.ValidationException;
import com.econest.model.Client;
import com.econest.model.HouseholdClient;
import com.econest.model.OfficeClient;
import com.econest.model.service.ClientService;
import com.econest.view.ClientPanel;

/** Connects ClientPanel (view) to ClientService (model). */
public class ClientController {

    private final ClientPanel view;
    private final ClientService service;

    public ClientController(ClientPanel view, ClientService service) {
        this.view = view;
        this.service = service;

        view.onAdd(e -> addClient());
        view.onUpdate(e -> updateClient());
        view.onDelete(e -> deleteClient());
        view.onClear(e -> view.clearForm());
        view.onSearch(e -> search());
        view.onShowAll(e -> refresh());
        view.onRowSelected(this::loadSelected);
        refresh();
    }

    public void refresh() {
        try {
            view.setClients(service.getAllClients());
        } catch (DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void addClient() {
        try {
            service.addClient(buildFromForm(0));
            view.showMessage("Client added successfully.");
            view.clearForm();
            refresh();
        } catch (ValidationException | DuplicateRecordException | DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void updateClient() {
        Client selected = view.getSelectedClient();
        if (selected == null) {
            view.showError("Select a client in the table first.");
            return;
        }
        try {
            service.updateClient(buildFromForm(selected.getId()));
            view.showMessage("Client updated successfully.");
            refresh();
        } catch (ValidationException | DuplicateRecordException | RecordNotFoundException
                 | DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void deleteClient() {
        Client selected = view.getSelectedClient();
        if (selected == null) {
            view.showError("Select a client in the table first.");
            return;
        }
        if (!view.confirm("Delete '" + selected.getName() + "'?")) {
            return;
        }
        try {
            service.deleteClient(selected.getId());
            view.showMessage("Client deleted.");
            view.clearForm();
            refresh();
        } catch (RecordNotFoundException | DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void search() {
        try {
            view.setClients(service.searchClients(view.getSearchText()));
        } catch (DatabaseException ex) {
            view.showError(ex.getMessage());
        }
    }

    private void loadSelected() {
        Client selected = view.getSelectedClient();
        if (selected != null) {
            view.populateForm(selected);
        }
    }

    private Client buildFromForm(int id) {
        String name = view.getNameInput();
        String email = view.getEmailInput();
        String phone = view.getPhoneInput();
        String address = view.getAddressInput();
        if ("Office".equals(view.getSelectedType())) {
            return new OfficeClient(id, name, email, phone, address, view.getDetail1(), view.getDetail2());
        }
        return new HouseholdClient(id, name, email, phone, address, parseInt(view.getDetail1()));
    }

    private int parseInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidationException("Household size must be a whole number.");
        }
    }
}
