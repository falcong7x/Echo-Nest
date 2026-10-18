package com.econest.model.dao;

import com.econest.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    int insert(Product product);

    void update(Product product);

    void delete(int productId);

    Optional<Product> findById(int productId);

    Optional<Product> findByName(String name);

    List<Product> findAll();

    List<Product> search(String keyword);
}
