package com.dineeasy.service;

import com.dineeasy.model.CartItem;
import com.dineeasy.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages the in-memory shopping cart for the current session.
 * No database interaction — the cart is transient and cleared after checkout.
 */
public class CartService {

    private final List<CartItem> cartItems = new ArrayList<>();

    /**
     * Adds a product to the cart.
     * If the product is already present its quantity is incremented.
     *
     * @return result message prefixed with "OK:" or "FAIL:"
     */
    public String addToCart(Product product, int quantity) {
        if (product == null)  return "FAIL:Product not found.";
        if (quantity <= 0)    return "FAIL:Quantity must be at least 1.";
        if (quantity > product.getQuantity())
            return "FAIL:Only " + product.getQuantity() + " unit(s) available in stock.";

        Optional<CartItem> existing = findCartItem(product.getId());
        if (existing.isPresent()) {
            int newQty = existing.get().getQuantity() + quantity;
            if (newQty > product.getQuantity()) {
                return "FAIL:Cannot add " + quantity + " more. Only " +
                       (product.getQuantity() - existing.get().getQuantity()) + " additional unit(s) available.";
            }
            existing.get().setQuantity(newQty);
            return "OK:Updated quantity for '" + product.getName() + "' to " + newQty + ".";
        }

        cartItems.add(new CartItem(product, quantity));
        return "OK:'" + product.getName() + "' (x" + quantity + ") added to cart.";
    }

    /**
     * Updates the quantity of an existing cart item.
     * Passing 0 removes the item.
     *
     * @return result message prefixed with "OK:" or "FAIL:"
     */
    public String updateQuantity(int productId, int newQuantity) {
        Optional<CartItem> item = findCartItem(productId);
        if (item.isEmpty()) return "FAIL:Item not found in cart.";

        if (newQuantity <= 0) return removeFromCart(productId);

        if (newQuantity > item.get().getProduct().getQuantity()) {
            return "FAIL:Only " + item.get().getProduct().getQuantity() + " unit(s) available in stock.";
        }

        item.get().setQuantity(newQuantity);
        return "OK:Quantity updated to " + newQuantity + " for '" + item.get().getProduct().getName() + "'.";
    }

    /**
     * Removes a product from the cart.
     *
     * @return result message prefixed with "OK:" or "FAIL:"
     */
    public String removeFromCart(int productId) {
        Optional<CartItem> item = findCartItem(productId);
        if (item.isEmpty()) return "FAIL:Item not in cart.";
        cartItems.remove(item.get());
        return "OK:'" + item.get().getProduct().getName() + "' removed from cart.";
    }

    /** Returns a snapshot of the current cart contents. */
    public List<CartItem> getCartItems() { return new ArrayList<>(cartItems); }

    /** Returns {@code true} if the cart is empty. */
    public boolean isEmpty() { return cartItems.isEmpty(); }

    /** Returns the grand total for all items in the cart. */
    public double calculateTotal() {
        return cartItems.stream().mapToDouble(CartItem::getSubTotal).sum();
    }

    /** Clears the cart (called after a successful order). */
    public void clearCart() { cartItems.clear(); }

    private Optional<CartItem> findCartItem(int productId) {
        return cartItems.stream()
                .filter(c -> c.getProduct().getId() == productId)
                .findFirst();
    }
}
