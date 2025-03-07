package com.example.easybuy.Database.DAO;

import android.content.Context;
import android.net.Uri;

import com.example.easybuy.Database.DatabaseHelper.DatabaseHelper;
import com.example.easybuy.Database.DatabaseHelper.ProductDatabaseHelper;
import com.example.easybuy.Model.Product;
import com.example.easybuy.Model.ProductImage;

import java.util.List;

public class ProductDAO {
    private final ProductDatabaseHelper productDbHelper;
    private final Context context;

    public ProductDAO(Context context) {
        this.context = context;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.productDbHelper = new ProductDatabaseHelper(dbHelper);
    }

    public Context getContext() {
        return context;
    }

    public boolean isProductOwner(int productId, int adminId) {
        return productDbHelper.isProductOwner(productId, adminId);
    }

    public long addProductWithImages(Product product, List<String> additionalImageUrls, int adminId) {
        return productDbHelper.addProductWithImages(product, additionalImageUrls, adminId);
    }

    public long addProduct(Product product, int adminId) {
        return productDbHelper.addProduct(product.getProductName(), product.getPrice(),
                product.getImageUrl(), product.getDescription(), adminId);
    }

    public long addProductImage(int productId, String imageUrl) {
        return productDbHelper.addProductImage(productId, imageUrl);
    }

    public int updateProduct(Product product, int adminId) {
        return productDbHelper.updateProduct(product, adminId);
    }

    public boolean deleteProduct(int productId, int adminId) {
        return productDbHelper.deleteProduct(productId, adminId);
    }

    public List<ProductImage> getProductImages(int productId) {
        return productDbHelper.getProductImages(productId);
    }

    public List<Product> getAllProducts() {
        return productDbHelper.getAllProducts();
    }

    public List<Product> getProductsByAdmin(int adminId) {
        return productDbHelper.getProductsByAdmin(adminId);
    }

    public void updateProductImages(int productId, List<Uri> imageUris) {
        productDbHelper.updateProductImages(productId, imageUris);
    }

    public Product getProductById(int productId) {
        return productDbHelper.getProductById(productId);
    }
}