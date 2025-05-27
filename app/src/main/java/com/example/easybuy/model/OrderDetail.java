package com.example.easybuy.model;

public class OrderDetail {
    private int detailId;
    private int orderId;
    private int productId;
    private int quantity;
    private double price;

    // Constructors, Getters, Setters
    public OrderDetail() {}
    public OrderDetail(int detailId, int orderId, int productId, int quantity, double price) {
        this.detailId = detailId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}