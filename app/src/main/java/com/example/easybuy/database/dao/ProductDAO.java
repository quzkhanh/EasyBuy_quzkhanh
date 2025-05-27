package com.example.easybuy.database.dao;

import android.content.Context;
import android.net.Uri;

import com.example.easybuy.database.helper.DatabaseHelper;
import com.example.easybuy.database.helper.ProductDatabaseHelper;
import com.example.easybuy.model.Product;
import com.example.easybuy.model.ProductImage;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final ProductDatabaseHelper productDbHelper;

    public ProductDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        productDbHelper = new ProductDatabaseHelper(dbHelper);
    }

    public Product getProductById(int productId) {
        return productDbHelper.getProductById(productId);
    }

    public List<String> getAdditionalImages(int productId) {
        List<ProductImage> productImages = productDbHelper.getProductImages(productId);
        List<String> imageUrls = new ArrayList<>();
        for (ProductImage image : productImages) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }

    public List<ProductImage> getProductImages(int productId) {
        return productDbHelper.getProductImages(productId);
    }

    public int updateProduct(Product product, int adminId) {
        return productDbHelper.updateProduct(product, adminId);
    }

    public void updateProductImages(int productId, List<Uri> imageUris) {
        productDbHelper.updateProductImages(productId, imageUris);
    }

    public boolean deleteProduct(int productId, int adminId) {
        return productDbHelper.deleteProduct(productId, adminId);
    }

    public long addProduct(String productName, double price, String imageUrl, String description, int adminId) {
        return productDbHelper.addProduct(productName, price, imageUrl, description, adminId);
    }

    public long addProductWithImages(Product product, List<String> additionalImageUrls, int adminId) {
        return productDbHelper.addProductWithImages(product, additionalImageUrls, adminId);
    }

    public long addProductImage(int productId, String imageUrl) {
        return productDbHelper.addProductImage(productId, imageUrl);
    }

    public List<Product> getAllProducts() {
        return productDbHelper.getAllProducts();
    }

    public List<Product> getProductsByAdmin(int adminId) {
        return productDbHelper.getProductsByAdmin(adminId);
    }

    public boolean isProductOwner(int productId, int adminId) {
        return productDbHelper.isProductOwner(productId, adminId);
    }
}