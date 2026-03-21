package com.dineeasy.dao;

import com.dineeasy.model.Order;
import com.dineeasy.model.OrderItem;
import java.util.List;

/** DAO interface for order and order-item operations. */
public interface OrderDAO {

    /**
     * Persists a new order header.
     *
     * @return the generated order id, or -1 on failure
     */
    int placeOrder(Order order);

    /**
     * Persists all line items for an order.
     *
     * @return {@code true} if all rows were inserted
     */
    boolean saveOrderItems(List<OrderItem> items, int orderId);

    /**
     * Retrieves order history for a specific user (newest first).
     *
     * @param userId owner's id
     */
    List<Order> getOrdersByUser(int userId);

    /** Retrieves all orders in the system (admin view). */
    List<Order> getAllOrders();

    /**
     * Fetches line items for a specific order with product names populated.
     *
     * @param orderId parent order id
     */
    List<OrderItem> getOrderItems(int orderId);
}
