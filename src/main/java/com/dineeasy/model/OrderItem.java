package com.dineeasy.model;

/**
 * Represents a single persisted line-item within an order.
 * {@code productName} is denormalised for display purposes.
 * {@code price} captures the unit price at the time of order.
 */
public class OrderItem {

    private int    id;
    private int    orderId;
    private int    productId;
    private String productName;
    private int    quantity;
    private double price;

    public OrderItem() {}

    public OrderItem(int orderId, int productId, int quantity, double price) {
        this.orderId   = orderId;
        this.productId = productId;
        this.quantity  = quantity;
        this.price     = price;
    }

    /** Returns the sub-total for this line (unit price × quantity). */
    public double getSubTotal() { return price * quantity; }

    public int    getId()                   { return id;              }
    public void   setId(int id)             { this.id = id;           }

    public int    getOrderId()              { return orderId;         }
    public void   setOrderId(int orderId)   { this.orderId = orderId; }

    public int    getProductId()                    { return productId;           }
    public void   setProductId(int productId)       { this.productId = productId; }

    public String getProductName()                  { return productName;         }
    public void   setProductName(String name)       { this.productName = name;    }

    public int    getQuantity()             { return quantity;        }
    public void   setQuantity(int q)        { this.quantity = q;      }

    public double getPrice()                { return price;           }
    public void   setPrice(double price)    { this.price = price;     }
}
