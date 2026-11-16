package com.econest.model.dao;

import com.econest.model.exception.DatabaseException;
import com.econest.model.GardeningKit;
import com.econest.model.Plant;
import com.econest.model.Planter;
import com.econest.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {

    private static final String COLS = "name, category, price, eco_rating, species, sunlight_needs, "
            + "watering_frequency, material, size, kit_contents, bundle_discount";

    @Override
    public int insert(Product p) {
        String sql = "INSERT INTO product (" + COLS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, p);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : -1;
                p.setId(id);
                try (PreparedStatement inventory = c.prepareStatement(
                        "INSERT OR IGNORE INTO inventory(product_id, quantity, reorder_level) VALUES(?,0,5)")) {
                    inventory.setInt(1, id);
                    inventory.executeUpdate();
                }
                return id;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not save product: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Product p) {
        String sql = "UPDATE product SET name=?, category=?, price=?, eco_rating=?, species=?, sunlight_needs=?, "
                + "watering_frequency=?, material=?, size=?, kit_contents=?, bundle_discount=? WHERE product_id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            ps.setInt(12, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not update product: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int productId) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM product WHERE product_id=?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not delete product. It may be used by inventory or orders.", e);
        }
    }

    @Override
    public Optional<Product> findById(int productId) {
        return queryOne("SELECT * FROM product WHERE product_id=?", String.valueOf(productId));
    }

    @Override
    public Optional<Product> findByName(String name) {
        return queryOne("SELECT * FROM product WHERE LOWER(name)=LOWER(?)", name);
    }

    @Override
    public List<Product> findAll() {
        return queryList("SELECT * FROM product ORDER BY product_id", null);
    }

    @Override
    public List<Product> search(String keyword) {
        String like = "%" + keyword.trim() + "%";
        return queryList("SELECT * FROM product WHERE name LIKE ? OR category LIKE ? ORDER BY product_id", like);
    }

    // ---- helpers ----

    private Optional<Product> queryOne(String sql, String param) {
        List<Product> result = queryList(sql, param);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    private List<Product> queryList(String sql, String param) {
        List<Product> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
                if (sql.contains("category LIKE ?")) {
                    ps.setString(2, param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not read products: " + e.getMessage(), e);
        }
        return list;
    }

    private void bind(PreparedStatement ps, Product p) throws SQLException {
        String species = null, sun = null, water = null, material = null, size = null, contents = null;
        Double discount = null;
        if (p instanceof Plant pl) {
            species = pl.getSpecies();
            sun = pl.getSunlightNeeds();
            water = pl.getWateringFrequency();
        } else if (p instanceof Planter pt) {
            material = pt.getMaterial();
            size = pt.getSize();
        } else if (p instanceof GardeningKit k) {
            contents = k.getContents();
            discount = k.getBundleDiscountPercent();
        }
        ps.setString(1, p.getName());
        ps.setString(2, p.getCategory());
        ps.setDouble(3, p.getPrice());
        ps.setInt(4, p.getEcoRating());
        ps.setString(5, species);
        ps.setString(6, sun);
        ps.setString(7, water);
        ps.setString(8, material);
        ps.setString(9, size);
        ps.setString(10, contents);
        ps.setObject(11, discount);
    }

    private Product map(ResultSet rs) throws SQLException {
        int id = rs.getInt("product_id");
        String name = rs.getString("name");
        double price = rs.getDouble("price");
        int eco = rs.getInt("eco_rating");
        String category = rs.getString("category");
        return switch (category) {
            case "Plant" -> new Plant(id, name, price, eco, rs.getString("species"),
                    rs.getString("sunlight_needs"), rs.getString("watering_frequency"));
            case "Planter" -> new Planter(id, name, price, eco, rs.getString("material"), rs.getString("size"));
            case "Gardening Kit" -> new GardeningKit(id, name, price, eco, rs.getString("kit_contents"),
                    rs.getDouble("bundle_discount"));
            default -> throw new DatabaseException("Unknown product category: " + category);
        };
    }
}
