package com.example.easybuy.Database.DatabaseHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.easybuy.Model.Product;
import com.example.easybuy.Model.ProductImage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProductDatabaseHelper {
    private static final String TAG = "ProductDatabaseHelper";
    private final DatabaseHelper dbHelper;

    public ProductDatabaseHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase();
    }

    public boolean isProductOwner(int productId, int adminId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCT +
                            " WHERE product_id = ? AND created_by = ?",
                    new String[]{String.valueOf(productId), String.valueOf(adminId)});
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking product owner", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    public long addProduct(String productName, double price, String imageUrl, String description, int adminId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", productName);
        values.put("price", price);
        values.put("image_url", imageUrl);
        values.put("description", description);
        values.put("created_by", adminId);
        long id = db.insert(DatabaseHelper.TABLE_PRODUCT, null, values);
        db.close();
        return id;
    }

    public long addProductWithImages(Product product, List<String> additionalImageUrls, int adminId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", product.getProductName());
        values.put("price", product.getPrice());
        values.put("image_url", product.getImageUrl());
        values.put("description", product.getDescription());
        values.put("created_by", adminId);

        long productId = db.insert(DatabaseHelper.TABLE_PRODUCT, null, values);

        if (productId != -1 && additionalImageUrls != null) {
            for (String imageUrl : additionalImageUrls) {
                ContentValues imageValues = new ContentValues();
                imageValues.put("product_id", productId);
                imageValues.put("image_url", imageUrl);
                db.insert(DatabaseHelper.TABLE_PRODUCT_IMAGES, null, imageValues);
            }
        }
        db.close();
        return productId;
    }

    public long addProductImage(int productId, String imageUrl) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("image_url", imageUrl);
        long id = db.insert(DatabaseHelper.TABLE_PRODUCT_IMAGES, null, values);
        db.close();
        return id;
    }

    public int updateProduct(Product product, int adminId) {
        if (!isProductOwner(product.getProductId(), adminId)) {
            Log.w(TAG, "Admin " + adminId + " does not have permission to update product " + product.getProductId());
            return 0;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", product.getProductName());
        values.put("price", product.getPrice());
        values.put("image_url", product.getImageUrl());
        values.put("description", product.getDescription());
        Log.d(TAG, "Updating product - Product ID: " + product.getProductId() + ", Image URL: " + product.getImageUrl());
        int rowsAffected = db.update(DatabaseHelper.TABLE_PRODUCT, values, "product_id = ?",
                new String[]{String.valueOf(product.getProductId())});
        Log.d(TAG, "Rows affected: " + rowsAffected);
        db.close();
        return rowsAffected;
    }

    public boolean deleteProduct(int productId, int adminId) {
        if (!isProductOwner(productId, adminId)) {
            Log.w(TAG, "Admin " + adminId + " does not have permission to delete product " + productId);
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_PRODUCT, "product_id = ?",
                new String[]{String.valueOf(productId)});
        db.close();
        return rowsAffected > 0;
    }

    public List<ProductImage> getProductImages(int productId) {
        List<ProductImage> imageList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCT_IMAGES +
                    " WHERE product_id = ?", new String[]{String.valueOf(productId)});

            HashSet<String> uniqueUrls = new HashSet<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ProductImage image = new ProductImage();
                    image.setImageId(cursor.getInt(cursor.getColumnIndexOrThrow("image_id")));
                    image.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                    String url = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                    if (!TextUtils.isEmpty(url) && !uniqueUrls.contains(url)) {
                        image.setImageUrl(url);
                        imageList.add(image);
                        uniqueUrls.add(url);
                        Log.d(TAG, "Added ProductImage: " + image.toString());
                    } else {
                        Log.w(TAG, "Skipped invalid or duplicate URL: " + (url == null ? "null" : url));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting product images", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return imageList;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCT, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Product product = extractProductFromCursor(cursor);
                    productList.add(product);
                    Log.d(TAG, "Fetched Product: " + product.toString());
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all products", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return productList;
    }

    public List<Product> getProductsByAdmin(int adminId) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCT +
                    " WHERE created_by = ?", new String[]{String.valueOf(adminId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Product product = extractProductFromCursor(cursor);
                    productList.add(product);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting products by admin", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return productList;
    }

    public void updateProductImages(int productId, List<Uri> imageUris) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_PRODUCT_IMAGES, "product_id = ?",
                new String[]{String.valueOf(productId)});
        for (Uri uri : imageUris) {
            ContentValues values = new ContentValues();
            values.put("product_id", productId);
            values.put("image_url", uri.toString());
            db.insert(DatabaseHelper.TABLE_PRODUCT_IMAGES, null, values);
        }
        db.close();
    }

    public Product getProductById(int productId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        Product product = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCT +
                    " WHERE product_id = ?", new String[]{String.valueOf(productId)});

            if (cursor != null && cursor.moveToFirst()) {
                product = extractProductFromCursor(cursor);
            } else {
                Log.w(TAG, "Product not found with ID: " + productId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving product by ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return product;
    }

    private Product extractProductFromCursor(Cursor cursor) {
        Product product = new Product();
        product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
        product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
        product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
        product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
        product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        product.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow("created_by")));
        return product;
    }
}