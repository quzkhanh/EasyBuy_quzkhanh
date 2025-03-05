package com.example.easybuy.Model;

public class ProductImage {
    private int imageId;
    private int productId;
    private String imageUrl;

    // Constructor với tham số
    public ProductImage(int imageId, int productId, String imageUrl) {
        this.imageId = imageId;
        this.productId = productId;
        setImageUrl(imageUrl); // Sử dụng setter để kiểm tra
    }

    // Constructor không tham số
    public ProductImage() {
    }

    // Getters và Setters
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : ""; // Trả về chuỗi rỗng nếu null
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = (imageUrl != null && !imageUrl.trim().isEmpty()) ? imageUrl.trim() : null;
    }

    @Override
    public String toString() {
        return "ProductImage{" +
                "imageId=" + imageId +
                ", productId=" + productId +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    // Phương thức equals và hashCode (tùy chọn, để so sánh ảnh)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductImage that = (ProductImage) o;
        return imageId == that.imageId &&
                productId == that.productId &&
                (imageUrl == null ? that.imageUrl == null : imageUrl.equals(that.imageUrl));
    }

    @Override
    public int hashCode() {
        int result = imageId;
        result = 31 * result + productId;
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }
}