package com.dineeasy.dao;

import com.dineeasy.model.Product;
import java.util.List;

/** DAO interface for product (menu item) operations. */
public interface ProductDAO {

    List<Product> getAllProducts();

    /** @return the matching {@link Product}, or {@code null} if not found */
    Product findById(int id);

    /** @return {@code true} on success */
    boolean addProduct(Product product);

    /** @return {@code true} on success */
    boolean updateProduct(Product product);

    /** @return {@code true} on success */
    boolean deleteProduct(int id);

    /**
     * Decrements stock after a successful order.
     *
     * @param productId product whose stock to reduce
     * @param quantity  units sold
     * @return {@code true} on success
     */
    boolean updateQuantity(int productId, int quantity);
}
