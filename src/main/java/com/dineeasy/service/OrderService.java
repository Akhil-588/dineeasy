package com.dineeasy.service;

import com.dineeasy.dao.OrderDAO;
import com.dineeasy.dao.OrderDAOImpl;
import com.dineeasy.model.*;

import java.util.ArrayList;
import java.util.List;

/** Service layer for placing orders and retrieving order history. */
public class OrderService {

    private final OrderDAO       orderDAO;
    private final ProductService productService;

    public OrderService() {
        this.orderDAO       = new OrderDAOImpl();
        this.productService = new ProductService();
    }

    /**
     * Places an order from the current cart.
     * Re-validates stock, persists the order and line items, then deducts inventory.
     *
     * @param userId    the ordering user
     * @param cartItems cart snapshot to convert to an order
     * @return the placed {@link Order} on success, {@code null} on failure
     */
    public Order placeOrder(int userId, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return null;

        for (CartItem ci : cartItems) {
            if (!productService.isStockAvailable(ci.getProduct().getId(), ci.getQuantity())) {
                System.out.println("  [!] Insufficient stock for: " + ci.getProduct().getName());
                return null;
            }
        }

        double total = cartItems.stream().mapToDouble(CartItem::getSubTotal).sum();
        Order order  = new Order(userId, total);

        int orderId = orderDAO.placeOrder(order);
        if (orderId == -1) {
            System.out.println("  [!] Failed to save order. Please try again.");
            return null;
        }

        List<OrderItem> items = new ArrayList<>();
        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem(orderId, ci.getProduct().getId(),
                                         ci.getQuantity(), ci.getProduct().getPrice());
            oi.setProductName(ci.getProduct().getName());
            items.add(oi);
        }

        if (!orderDAO.saveOrderItems(items, orderId)) {
            System.out.println("  [!] Failed to save order items.");
            return null;
        }

        for (CartItem ci : cartItems) {
            productService.deductStock(ci.getProduct().getId(), ci.getQuantity());
        }

        order.setId(orderId);
        order.setItems(items);
        return order;
    }

    /**
     * Returns the order history for a user, with line items populated.
     *
     * @param userId user whose history to fetch
     */
    public List<Order> getOrderHistory(int userId) {
        List<Order> orders = orderDAO.getOrdersByUser(userId);
        for (Order o : orders) o.setItems(orderDAO.getOrderItems(o.getId()));
        return orders;
    }

    /** Returns all orders in the system with line items populated (admin). */
    public List<Order> getAllOrders() {
        List<Order> orders = orderDAO.getAllOrders();
        for (Order o : orders) o.setItems(orderDAO.getOrderItems(o.getId()));
        return orders;
    }
}
