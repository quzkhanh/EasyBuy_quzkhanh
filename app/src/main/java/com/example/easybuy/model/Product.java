    package com.example.easybuy.model;
    import com.google.gson.annotations.SerializedName;

    public class Product {
        private int productId;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("price")
        private double price;

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("description")
        private String description;

        @SerializedName("created_by")
        private int createdBy;
        // Default constructor
        public Product() {}

        // Constructor for adding a new product (no productId since it's auto-incremented)
        public Product(String productName, double price, String imageUrl, String description, int createdBy) {
            this.productName = productName;
            this.price = price;
            this.imageUrl = imageUrl;
            this.description = description;
            this.createdBy = createdBy;
        }

        // Full constructor (including productId for retrieval from DB)
        public Product(int productId, String productName, double price, String imageUrl, String description, int createdBy) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.imageUrl = imageUrl;
            this.description = description;
            this.createdBy = createdBy;
        }

        // Getters and Setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public int getCreatedBy() { return createdBy; }
        public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    }