package com.econest.model.dao;

import com.econest.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDAO {
    int insert(Client client);

    void update(Client client);

    void delete(int clientId);

    Optional<Client> findById(int clientId);

    Optional<Client> findByEmail(String email);

    List<Client> findAll();

    List<Client> search(String keyword);
}
