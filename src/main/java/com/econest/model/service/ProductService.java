package com.econest.model.service;

import com.econest.model.dao.ProductDAO;
import com.econest.model.exception.DuplicateRecordException;
import com.econest.model.exception.RecordNotFoundException;
import com.econest.model.exception.ValidationException;
import com.econest.model.Product;

import java.util.List;

/** Business rules for the product catalogue. Other modules (inventory, orders) call this class. */
public class ProductService {

    private final ProductDAO dao;

    public ProductService(ProductDAO dao) {
        this.dao = dao;
    }

    public int addProduct(Product product) {
        if (product == null) {
            throw new ValidationException("Product details are required.");
        }
        dao.findByName(product.getName()).ifPresent(existing -> {
            throw new DuplicateRecordException("A product named '" + product.getName() + "' already exists.");
        });
        return dao.insert(product);
    }

    public void updateProduct(Product product) {
        if (product == null || product.getId() <= 0) {
            throw new ValidationException("Select a product to update.");
        }
        getProductById(product.getId());
        dao.findByName(product.getName()).filter(other -> other.getId() != product.getId()).ifPresent(other -> {
            throw new DuplicateRecordException("Another product is already named '" + product.getName() + "'.");
        });
        dao.update(product);
    }

    public void deleteProduct(int productId) {
        getProductById(productId);
        dao.delete(productId);
    }

    public Product getProductById(int productId) {
        return dao.findById(productId)
                .orElseThrow(() -> new RecordNotFoundException("Product " + productId + " was not found."));
    }

    public List<Product> getAllProducts() {
        return dao.findAll();
    }

    public List<Product> searchProducts(String keyword) {
        return (keyword == null || keyword.isBlank()) ? dao.findAll() : dao.search(keyword);
    }
}
