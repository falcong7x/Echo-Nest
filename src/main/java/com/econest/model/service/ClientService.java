package com.econest.model.service;

import com.econest.model.dao.ClientDAO;
import com.econest.model.exception.DuplicateRecordException;
import com.econest.model.exception.RecordNotFoundException;
import com.econest.model.exception.ValidationException;
import com.econest.model.Client;

import java.util.List;

/** Business rules for client management. Email tasks (8, 9) and orders use getClientById(). */
public class ClientService {

    private final ClientDAO dao;

    public ClientService(ClientDAO dao) {
        this.dao = dao;
    }

    public int addClient(Client client) {
        if (client == null) {
            throw new ValidationException("Client details are required.");
        }
        dao.findByEmail(client.getEmail()).ifPresent(existing -> {
            throw new DuplicateRecordException("A client with email '" + client.getEmail() + "' already exists.");
        });
        return dao.insert(client);
    }

    public void updateClient(Client client) {
        if (client == null || client.getId() <= 0) {
            throw new ValidationException("Select a client to update.");
        }
        getClientById(client.getId());
        dao.findByEmail(client.getEmail()).filter(other -> other.getId() != client.getId()).ifPresent(other -> {
            throw new DuplicateRecordException("Another client already uses '" + client.getEmail() + "'.");
        });
        dao.update(client);
    }

    public void deleteClient(int clientId) {
        getClientById(clientId);
        dao.delete(clientId);
    }

    public Client getClientById(int clientId) {
        return dao.findById(clientId)
                .orElseThrow(() -> new RecordNotFoundException("Client " + clientId + " was not found."));
    }

    public List<Client> getAllClients() {
        return dao.findAll();
    }

    public List<Client> searchClients(String keyword) {
        return (keyword == null || keyword.isBlank()) ? dao.findAll() : dao.search(keyword);
    }
}
