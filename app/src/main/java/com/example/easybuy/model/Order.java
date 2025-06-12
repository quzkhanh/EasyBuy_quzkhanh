package com.example.easybuy.model;

public class Order {
    private int orderId;
    private int userId;
    private int productId;
    private String productName; // Thêm productName
    private int quantity;
    private double totalPrice;
    private String status;
    private String orderDate;
    private String shippingAddress;
    private String phoneNumber;
    private String paymentMethod;

    // Default constructor
    public Order() {}

    // Full constructor
    public Order(int orderId, int userId, int productId, String productName, int quantity, double totalPrice,
                 String status, String orderDate, String shippingAddress,
                 String phoneNumber, String paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderDate = orderDate;
        this.shippingAddress = shippingAddress;
        this.phoneNumber = phoneNumber;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    // Hàm getName trả về productName
    public String getName() {
        return productName != null ? productName : "Không có tên sản phẩm";
    }
}