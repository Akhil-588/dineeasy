package com.dineeasy.main;

import com.dineeasy.model.*;
import com.dineeasy.service.*;
import com.dineeasy.util.DatabaseConnection;
import com.dineeasy.util.InputValidator;

import java.util.List;

public class DineEasyApp {

    private static final UserService    userService    = new UserService();
    private static final ProductService productService = new ProductService();
    private static final CartService    cartService    = new CartService();
    private static final OrderService   orderService   = new OrderService();

    private static User currentUser = null;

    public static void main(String[] args) {
        printBanner();
        try {
            runAuthMenu();
        } finally {
            DatabaseConnection.closeConnection();
            System.out.println("\n  Thank you for using DineEasy! Goodbye.\n");
        }
    }

    private static void runAuthMenu() {
        while (true) {
            printSectionHeader("WELCOME");
            System.out.println("  1. Login");
            System.out.println("  2. Register");
            System.out.println("  0. Exit");
            printDivider();

            int choice = InputValidator.readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegister();
                case 0 -> { return; }
                default -> printError("Invalid option. Please choose 1, 2, or 0.");
            }
        }
    }

    private static void handleLogin() {
        printSectionHeader("LOGIN");
        String username = InputValidator.readNonEmptyString("  Username : ");
        String password = InputValidator.readNonEmptyString("  Password : ");

        User user = userService.login(username, password);
        if (user == null) {
            printError("Invalid credentials. Please try again.");
            return;
        }

        currentUser = user;
        printSuccess("Logged in as " + user.getUsername() + " [" + user.getRole() + "]");

        if (user.isAdmin()) {
            runAdminDashboard();
        } else {
            runUserDashboard();
        }
        currentUser = null;
    }

    private static void handleRegister() {
        printSectionHeader("REGISTER");
        String username = InputValidator.readNonEmptyString("  Choose a username (min 3 chars) : ");
        String password = InputValidator.readNonEmptyString("  Choose a password (min 6 chars) : ");

        String result = userService.register(username, password);
        if (result.startsWith("OK:")) {
            printSuccess(result.substring(3));
        } else {
            printError(result.substring(5));
        }
    }

    private static void runUserDashboard() {
        while (true) {
            printSectionHeader("USER DASHBOARD  –  " + currentUser.getUsername());
            System.out.println("  1. View Menu");
            System.out.println("  2. Add Item to Cart");
            System.out.println("  3. View Cart");
            System.out.println("  4. Update Cart Item Quantity");
            System.out.println("  5. Remove Item from Cart");
            System.out.println("  6. Checkout & Pay");
            System.out.println("  7. My Order History");
            System.out.println("  0. Logout");
            printDivider();

            int choice = InputValidator.readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> displayMenu();
                case 2 -> handleAddToCart();
                case 3 -> displayCart();
                case 4 -> handleUpdateCartQty();
                case 5 -> handleRemoveFromCart();
                case 6 -> handleCheckout();
                case 7 -> displayOrderHistory();
                case 0 -> {
                    cartService.clearCart();
                    printSuccess("Logged out successfully.");
                    return;
                }
                default -> printError("Invalid option.");
            }
        }
    }

    private static void displayMenu() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            printError("No items available on the menu at this time.");
            return;
        }
        printSectionHeader("TODAY'S MENU");
        System.out.printf("  %-4s  %-30s  %10s  %8s%n", "ID", "Item", "Price (₹)", "In Stock");
        printDivider();
        for (Product p : products) {
            System.out.printf("  %-4d  %-30s  %10.2f  %8d%n",
                    p.getId(), p.getName(), p.getPrice(), p.getQuantity());
        }
        printDivider();
    }

    private static void handleAddToCart() {
        displayMenu();
        int productId = InputValidator.readInt("  Enter Product ID to add: ");
        Product product = productService.getProductById(productId);
        if (product == null) {
            printError("Product ID " + productId + " not found.");
            return;
        }
        int qty = InputValidator.readInt("  Enter quantity: ");
        printResult(cartService.addToCart(product, qty));
    }

    private static void displayCart() {
        if (cartService.isEmpty()) {
            printError("Your cart is empty.");
            return;
        }
        printSectionHeader("YOUR CART");
        System.out.printf("  %-4s  %-30s  %8s  %10s  %12s%n",
                "ID", "Item", "Qty", "Unit (₹)", "Sub-total (₹)");
        printDivider();
        for (CartItem ci : cartService.getCartItems()) {
            System.out.printf("  %-4d  %-30s  %8d  %10.2f  %12.2f%n",
                    ci.getProduct().getId(), ci.getProduct().getName(),
                    ci.getQuantity(), ci.getProduct().getPrice(), ci.getSubTotal());
        }
        printDivider();
        System.out.printf("  %-55s  %12.2f%n", "TOTAL (₹)", cartService.calculateTotal());
        printDivider();
    }

    private static void handleUpdateCartQty() {
        if (cartService.isEmpty()) { printError("Your cart is empty."); return; }
        displayCart();
        int productId = InputValidator.readInt("  Enter Product ID to update: ");
        int newQty    = InputValidator.readInt("  Enter new quantity (0 to remove): ");
        printResult(cartService.updateQuantity(productId, newQty));
    }

    private static void handleRemoveFromCart() {
        if (cartService.isEmpty()) { printError("Your cart is empty."); return; }
        displayCart();
        int productId = InputValidator.readInt("  Enter Product ID to remove: ");
        printResult(cartService.removeFromCart(productId));
    }

    private static void handleCheckout() {
        if (cartService.isEmpty()) {
            printError("Cart is empty. Add items before checking out.");
            return;
        }

        displayCart();
        printSectionHeader("FINAL BILL");
        System.out.printf("  Customer  : %s%n", currentUser.getUsername());
        System.out.printf("  Items     : %d%n", cartService.getCartItems().size());
        System.out.printf("  Total     : ₹%.2f%n", cartService.calculateTotal());
        printDivider();

        String confirm = InputValidator.readNonEmptyString("  Confirm order and pay? (yes/no): ");
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("  Order cancelled. Your cart is preserved.");
            return;
        }

        System.out.println("\n  Processing payment...");
        simulatePayment();

        Order order = orderService.placeOrder(currentUser.getId(), cartService.getCartItems());
        if (order == null) {
            printError("Order could not be completed. Please try again.");
            return;
        }

        cartService.clearCart();
        printOrderReceipt(order);
    }

    private static void simulatePayment() {
        printSectionHeader("PAYMENT");
        System.out.println("  1. Cash on Delivery");
        System.out.println("  2. UPI");
        System.out.println("  3. Credit / Debit Card");
        int method = InputValidator.readInt("  Select payment method: ");
        String mode = switch (method) {
            case 1 -> "Cash on Delivery";
            case 2 -> "UPI";
            case 3 -> "Credit / Debit Card";
            default -> "Cash on Delivery";
        };
        System.out.println("  Payment mode  : " + mode);
        System.out.println("  Status        : ✔  PAYMENT SUCCESSFUL");
    }

    private static void printOrderReceipt(Order order) {
        printSectionHeader("ORDER CONFIRMED – RECEIPT");
        System.out.printf("  Order ID  : #%d%n", order.getId());
        System.out.printf("  Customer  : %s%n", currentUser.getUsername());
        System.out.printf("  Date      : %s%n", order.getOrderDate());
        printDivider();
        System.out.printf("  %-30s  %8s  %10s  %12s%n", "Item", "Qty", "Unit (₹)", "Sub-total (₹)");
        printDivider();
        if (order.getItems() != null) {
            for (OrderItem oi : order.getItems()) {
                System.out.printf("  %-30s  %8d  %10.2f  %12.2f%n",
                        oi.getProductName(), oi.getQuantity(), oi.getPrice(), oi.getSubTotal());
            }
        }
        printDivider();
        System.out.printf("  %-52s  %12.2f%n", "GRAND TOTAL (₹)", order.getTotalAmount());
        printDivider();
        System.out.println("  Thank you for your order!  Estimated delivery: 30–45 mins.");
        printDivider();
    }

    private static void displayOrderHistory() {
        List<Order> orders = orderService.getOrderHistory(currentUser.getId());
        if (orders.isEmpty()) {
            printError("No past orders found.");
            return;
        }
        printSectionHeader("MY ORDER HISTORY");
        for (Order o : orders) {
            System.out.printf("  Order #%-4d  |  Date: %-20s  |  Total: ₹%.2f%n",
                    o.getId(), o.getOrderDate(), o.getTotalAmount());
            if (o.getItems() != null) {
                for (OrderItem oi : o.getItems()) {
                    System.out.printf("       %-30s  x%d  @ ₹%.2f%n",
                            oi.getProductName(), oi.getQuantity(), oi.getPrice());
                }
            }
            printDivider();
        }
    }

    private static void runAdminDashboard() {
        while (true) {
            printSectionHeader("ADMIN DASHBOARD  –  " + currentUser.getUsername());
            System.out.println("  1. View Menu");
            System.out.println("  2. Add New Food Item");
            System.out.println("  3. Update Food Item");
            System.out.println("  4. Delete Food Item");
            System.out.println("  5. View All Orders");
            System.out.println("  0. Logout");
            printDivider();

            int choice = InputValidator.readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> displayMenu();
                case 2 -> adminAddProduct();
                case 3 -> adminUpdateProduct();
                case 4 -> adminDeleteProduct();
                case 5 -> adminViewAllOrders();
                case 0 -> {
                    printSuccess("Admin logged out.");
                    return;
                }
                default -> printError("Invalid option.");
            }
        }
    }

    private static void adminAddProduct() {
        printSectionHeader("ADD NEW FOOD ITEM");
        String name  = InputValidator.readNonEmptyString("  Item name   : ");
        double price = InputValidator.readDouble        ("  Price (₹)   : ");
        int    qty   = InputValidator.readInt           ("  Quantity    : ");
        printResult(productService.addProduct(name, price, qty));
    }

    private static void adminUpdateProduct() {
        displayMenu();
        int id = InputValidator.readInt("  Enter Product ID to update: ");
        if (productService.getProductById(id) == null) {
            printError("Product ID " + id + " not found.");
            return;
        }
        printSectionHeader("UPDATE FOOD ITEM (ID: " + id + ")");
        String name  = InputValidator.readNonEmptyString("  New name    : ");
        double price = InputValidator.readDouble        ("  New price   : ");
        int    qty   = InputValidator.readInt           ("  New quantity: ");
        printResult(productService.updateProduct(id, name, price, qty));
    }

    private static void adminDeleteProduct() {
        displayMenu();
        int id = InputValidator.readInt("  Enter Product ID to delete: ");
        String confirm = InputValidator.readNonEmptyString(
                "  Are you sure you want to delete product #" + id + "? (yes/no): ");
        if (confirm.equalsIgnoreCase("yes")) {
            printResult(productService.deleteProduct(id));
        } else {
            System.out.println("  Deletion cancelled.");
        }
    }

    private static void adminViewAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            printError("No orders in the system yet.");
            return;
        }
        printSectionHeader("ALL ORDERS");
        for (Order o : orders) {
            System.out.printf("  Order #%-4d  |  User ID: %-4d  |  Date: %-20s  |  Total: ₹%.2f%n",
                    o.getId(), o.getUserId(), o.getOrderDate(), o.getTotalAmount());
            if (o.getItems() != null) {
                for (OrderItem oi : o.getItems()) {
                    System.out.printf("       %-30s  x%d  @ ₹%.2f%n",
                            oi.getProductName(), oi.getQuantity(), oi.getPrice());
                }
            }
            printDivider();
        }
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║         DineEasy – Food Ordering & Billing           ║");
        System.out.println("  ║                  Version 1.0.0                       ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printSectionHeader(String title) {
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────┐");
        System.out.printf ("  │  %-52s│%n", title);
        System.out.println("  └──────────────────────────────────────────────────────┘");
    }

    private static void printDivider() {
        System.out.println("  ──────────────────────────────────────────────────────");
    }

    private static void printSuccess(String msg) { System.out.println("  ✔  " + msg); }
    private static void printError(String msg)   { System.out.println("  ✘  " + msg); }

    private static void printResult(String result) {
        if (result.startsWith("OK:")) {
            printSuccess(result.substring(3));
        } else if (result.startsWith("FAIL:")) {
            printError(result.substring(5));
        } else {
            System.out.println("  " + result);
        }
    }
}
