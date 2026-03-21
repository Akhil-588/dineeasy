package com.dineeasy.model;

/**
 * Represents a single line-item in the user's in-memory shopping cart.
 */
public class CartItem {

    private Product product;
    private int     quantity;

    public CartItem(Product product, int quantity) {
        this.product  = product;
        this.quantity = quantity;
    }

    /** Returns the sub-total for this cart line (unit price × quantity). */
    public double getSubTotal() {
        return product.getPrice() * quantity;
    }

    public Product getProduct()              { return product;           }
    public void    setProduct(Product p)     { this.product = p;         }

    public int     getQuantity()             { return quantity;          }
    public void    setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return String.format("CartItem{productId=%d, name='%s', qty=%d, subTotal=%.2f}",
                product.getId(), product.getName(), quantity, getSubTotal());
    }
}
