package com.dineeasy.model;

import java.time.LocalDateTime;
import java.util.List;

/** Represents a placed order header with optional line-item detail. */
public class Order {

    private int             id;
    private int             userId;
    private double          totalAmount;
    private LocalDateTime   orderDate;
    private List<OrderItem> items;

    public Order() {}

    public Order(int userId, double totalAmount) {
        this.userId      = userId;
        this.totalAmount = totalAmount;
        this.orderDate   = LocalDateTime.now();
    }

    public int           getId()                          { return id;              }
    public void          setId(int id)                    { this.id = id;           }

    public int           getUserId()                      { return userId;          }
    public void          setUserId(int userId)            { this.userId = userId;   }

    public double        getTotalAmount()                 { return totalAmount;     }
    public void          setTotalAmount(double v)         { this.totalAmount = v;   }

    public LocalDateTime getOrderDate()                   { return orderDate;       }
    public void          setOrderDate(LocalDateTime d)    { this.orderDate = d;     }

    public List<OrderItem> getItems()                     { return items;           }
    public void            setItems(List<OrderItem> items){ this.items = items;     }

    @Override
    public String toString() {
        return String.format("Order{id=%d, userId=%d, total=%.2f, date=%s}",
                id, userId, totalAmount, orderDate);
    }
}
