package com.example.easybuy.Model;

public class Product {
    private int productId;
    private String productName;
    private double price;
    private String imageUrl;
    private String description;
    private int createdBy;

    // Constructor đầy đủ
    public Product(int productId, String productName, double price, String imageUrl, String description, int createdBy) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Constructor không có productId, dùng khi thêm mới
    public Product(String productName, double price, String imageUrl, String description, int createdBy) {
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Constructor không tham số
    public Product() {
    }

    // Getters và Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}