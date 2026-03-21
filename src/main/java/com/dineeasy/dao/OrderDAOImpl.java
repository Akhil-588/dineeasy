package com.dineeasy.dao;

import com.dineeasy.model.Order;
import com.dineeasy.model.OrderItem;
import com.dineeasy.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of {@link OrderDAO}.
 * Order header and line items are written in separate statements;
 * wrap calls in a transaction if atomicity is required.
 */
public class OrderDAOImpl implements OrderDAO {

    private static final String INSERT_ORDER =
            "INSERT INTO orders (user_id, total_amount, order_date) VALUES (?, ?, NOW())";

    private static final String INSERT_ITEM =
            "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

    private static final String SELECT_BY_USER =
            "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";

    private static final String SELECT_ALL =
            "SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.order_date DESC";

    private static final String SELECT_ITEMS =
            "SELECT oi.*, p.name AS product_name FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";

    @Override
    public int placeOrder(Order order) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                order.setId(id);
                return id;
            }

        } catch (SQLException e) {
            System.err.println("Error placing order: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean saveOrderItems(List<OrderItem> items, int orderId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_ITEM)) {

            for (OrderItem item : items) {
                ps.setInt   (1, orderId);
                ps.setInt   (2, item.getProductId());
                ps.setInt   (3, item.getQuantity());
                ps.setDouble(4, item.getPrice());
                ps.addBatch();
            }
            for (int r : ps.executeBatch()) {
                if (r == 0) return false;
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving order items: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Order> getOrdersByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USER)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) orders.add(mapOrderRow(rs));

        } catch (SQLException e) {
            System.err.println("Error fetching user orders: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) orders.add(mapOrderRow(rs));

        } catch (SQLException e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ITEMS)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem(
                        orderId,
                        rs.getInt   ("product_id"),
                        rs.getInt   ("quantity"),
                        rs.getDouble("price")
                );
                item.setId         (rs.getInt   ("id"));
                item.setProductName(rs.getString("product_name"));
                items.add(item);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching order items: " + e.getMessage());
        }
        return items;
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId         (rs.getInt   ("id"));
        order.setUserId     (rs.getInt   ("user_id"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        Timestamp ts = rs.getTimestamp("order_date");
        if (ts != null) order.setOrderDate(ts.toLocalDateTime());
        return order;
    }
}
