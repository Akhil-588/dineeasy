package com.dineeasy.service;

import com.dineeasy.dao.ProductDAO;
import com.dineeasy.dao.ProductDAOImpl;
import com.dineeasy.model.Product;

import java.util.List;

/** Service layer for product (menu) business logic. */
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }

    /** Returns all available menu items. */
    public List<Product> getAllProducts() { return productDAO.getAllProducts(); }

    /**
     * Finds a product by id.
     *
     * @return the {@link Product}, or {@code null} if not found
     */
    public Product getProductById(int id) { return productDAO.findById(id); }

    /**
     * Adds a new menu item (admin).
     *
     * @return result message prefixed with "OK:" or "FAIL:"
     */
    public String addProduct(String name, double price, int quantity) {
        if (name == null || name.trim().isEmpty()) return "FAIL:Product name cannot be empty.";
        if (price <= 0)   return "FAIL:Price must be greater than 0.";
        if (quantity < 0) return "FAIL:Quantity cannot be negative.";

        Product p = new Product(name.trim(), price, quantity);
        return productDAO.addProduct(p)
                ? "OK:Product '" + name + "' added successfully (ID: " + p.getId() + ")."
                : "FAIL:Could not add product. Please try again.";
    }

    /**
     * Updates an existing menu item (admin).
     *
     * @return result message prefixed with "OK:" or "FAIL:"
     */
    public String updateProduct(int id, String name, double price, int quantity) {
        Product existing = productDAO.findById(id);
        if (existing == null)                      return "FAIL:No product found with ID " + id + ".";
        if (name == null || name.trim().isEmpty()) return "FAIL:Product name cannot be empty.";
        if (price <= 0)                            return "FAIL:Price must be greater than 0.";
        if (quantity < 0)                          return "FAIL:Quantity cannot be negative.";

        existing.setName    (name.trim());
        existing.setPrice   (price);
        existing.setQuantity(quantity);

        return productDAO.updateProduct(existing)
                ? "OK:Product updated successfully."
                : "FAIL:Update failed. Please try again.";
    }

    /**
     * Deletes a menu item (admin).
     *
     * @return result message prefixed with "OK:" or "FAIL:"
     */
    public String deleteProduct(int id) {
        if (productDAO.findById(id) == null) return "FAIL:No product found with ID " + id + ".";
        return productDAO.deleteProduct(id)
                ? "OK:Product deleted successfully."
                : "FAIL:Delete failed. Please try again.";
    }

    /**
     * Checks whether a product has sufficient stock for the requested quantity.
     *
     * @return {@code true} if stock is sufficient
     */
    public boolean isStockAvailable(int productId, int requested) {
        Product p = productDAO.findById(productId);
        return p != null && p.getQuantity() >= requested;
    }

    /** Decrements stock after a successful order. */
    public boolean deductStock(int productId, int quantity) {
        return productDAO.updateQuantity(productId, quantity);
    }
}
