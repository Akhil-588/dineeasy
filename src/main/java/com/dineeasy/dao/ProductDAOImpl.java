package com.dineeasy.dao;

import com.dineeasy.model.Product;
import com.dineeasy.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** JDBC implementation of {@link ProductDAO}. */
public class ProductDAOImpl implements ProductDAO {

    private static final String SELECT_ALL   = "SELECT * FROM products ORDER BY id";
    private static final String SELECT_BY_ID = "SELECT * FROM products WHERE id = ?";
    private static final String INSERT       = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";
    private static final String UPDATE       = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";
    private static final String DELETE       = "DELETE FROM products WHERE id = ?";
    private static final String UPDATE_QTY   = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Product findById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("Error finding product: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean addProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt   (3, product.getQuantity());

            if (ps.executeUpdate() > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) product.setId(keys.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt   (3, product.getQuantity());
            ps.setInt   (4, product.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteProduct(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateQuantity(int productId, int quantity) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_QTY)) {

            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating quantity: " + e.getMessage());
            return false;
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt   ("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getInt   ("quantity")
        );
    }
}
