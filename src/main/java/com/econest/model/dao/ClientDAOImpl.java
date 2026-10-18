package com.econest.model.dao;

import com.econest.model.exception.DatabaseException;
import com.econest.model.Client;
import com.econest.model.HouseholdClient;
import com.econest.model.OfficeClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAOImpl implements ClientDAO {

    @Override
    public int insert(Client cl) {
        String sql = "INSERT INTO client (name, client_type, email, phone, address, household_size, "
                + "company_name, contact_person) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, cl);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : -1;
                cl.setId(id);
                return id;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not save client: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Client cl) {
        String sql = "UPDATE client SET name=?, client_type=?, email=?, phone=?, address=?, household_size=?, "
                + "company_name=?, contact_person=? WHERE client_id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, cl);
            ps.setInt(9, cl.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not update client: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int clientId) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM client WHERE client_id=?")) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not delete client. They may have existing orders.", e);
        }
    }

    @Override
    public Optional<Client> findById(int clientId) {
        List<Client> r = queryList("SELECT * FROM client WHERE client_id=?", String.valueOf(clientId), false);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        List<Client> r = queryList("SELECT * FROM client WHERE LOWER(email)=LOWER(?)", email, false);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override
    public List<Client> findAll() {
        return queryList("SELECT * FROM client ORDER BY client_id", null, false);
    }

    @Override
    public List<Client> search(String keyword) {
        return queryList("SELECT * FROM client WHERE name LIKE ? OR email LIKE ? OR client_type LIKE ? "
                + "ORDER BY client_id", "%" + keyword.trim() + "%", true);
    }

    // ---- helpers ----

    private List<Client> queryList(String sql, String param, boolean threeParams) {
        List<Client> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
                if (threeParams) {
                    ps.setString(2, param);
                    ps.setString(3, param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not read clients: " + e.getMessage(), e);
        }
        return list;
    }

    private void bind(PreparedStatement ps, Client cl) throws SQLException {
        Integer size = null;
        String company = null, contact = null;
        if (cl instanceof HouseholdClient h) {
            size = h.getHouseholdSize();
        } else if (cl instanceof OfficeClient o) {
            company = o.getCompanyName();
            contact = o.getContactPerson();
        }
        ps.setString(1, cl.getName());
        ps.setString(2, cl.getClientType());
        ps.setString(3, cl.getEmail());
        ps.setString(4, cl.getPhone());
        ps.setString(5, cl.getAddress());
        ps.setObject(6, size);
        ps.setString(7, company);
        ps.setString(8, contact);
    }

    private Client map(ResultSet rs) throws SQLException {
        int id = rs.getInt("client_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String address = rs.getString("address");
        String type = rs.getString("client_type");
        return switch (type) {
            case "Household" -> new HouseholdClient(id, name, email, phone, address, rs.getInt("household_size"));
            case "Office" -> new OfficeClient(id, name, email, phone, address,
                    rs.getString("company_name"), rs.getString("contact_person"));
            default -> throw new DatabaseException("Unknown client type: " + type);
        };
    }
}
