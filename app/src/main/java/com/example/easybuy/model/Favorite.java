package com.example.easybuy.model;

public class Favorite {
    private int favoriteId;
    private int userId;
    private int productId;
    private String productName;
    private double price;
    private String imageUrl;

    // Constructor
    public Favorite(int favoriteId, int userId, int productId, String productName, double price, String imageUrl) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getFavoriteId() { return favoriteId; }
    public int getUserId() { return userId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}