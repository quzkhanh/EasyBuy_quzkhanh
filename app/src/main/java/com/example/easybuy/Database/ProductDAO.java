package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.easybuy.Model.Product;
import com.example.easybuy.Model.ProductImage;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private DatabaseHelper dbHelper;
    private Context context;

    private static final String TABLE_PRODUCT = "product";
    private static final String TABLE_PRODUCT_IMAGES = "product_images";

    public ProductDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public Context getContext() {
        return context;
    }

    // Phương thức kiểm tra quyền sở hữu sản phẩm
    public boolean isProductOwner(int productId, int adminId) {
        return dbHelper.isProductOwner(productId, adminId);
    }

    public long addProductWithImages(Product product, List<String> additionalImageUrls, int adminId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", product.getProductName());
        values.put("price", product.getPrice());
        values.put("image_url", product.getImageUrl());
        values.put("description", product.getDescription());
        values.put("created_by", adminId);

        long productId = db.insert(TABLE_PRODUCT, null, values);

        if (productId != -1 && additionalImageUrls != null) {
            for (String imageUrl : additionalImageUrls) {
                ContentValues imageValues = new ContentValues();
                imageValues.put("product_id", productId);
                imageValues.put("image_url", imageUrl);
                db.insert(TABLE_PRODUCT_IMAGES, null, imageValues);
            }
        }
        db.close();
        return productId;
    }

    public long addProduct(Product product, int adminId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", product.getProductName());
        values.put("price", product.getPrice());
        String imageUrl = product.getImageUrl();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            Log.w("ProductDAO", "Image URL is null or empty for product: " + product.getProductName());
            values.put("image_url", "");
        } else {
            values.put("image_url", imageUrl);
        }
        values.put("description", product.getDescription());
        values.put("created_by", adminId);

        long id = db.insert(TABLE_PRODUCT, null, values);
        Log.d("ProductDAO", "Added product with ID: " + id + ", Image URL: " + imageUrl);
        db.close();
        return id;
    }

    public long addProductImage(int productId, String imageUrl) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("image_url", imageUrl);
        long id = db.insert(TABLE_PRODUCT_IMAGES, null, values);
        db.close();
        return id;
    }

    public int updateProduct(Product product, int adminId) {
        if (!dbHelper.isProductOwner(product.getProductId(), adminId)) {
            Log.w("ProductDAO", "Admin " + adminId + " does not have permission to update product " + product.getProductId());
            return 0;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", product.getProductName());
        values.put("price", product.getPrice());
        values.put("image_url", product.getImageUrl());
        values.put("description", product.getDescription());
        int rowsAffected = db.update(TABLE_PRODUCT, values, "product_id = ?", new String[]{String.valueOf(product.getProductId())});
        db.close();
        return rowsAffected;
    }

    public boolean deleteProduct(int productId, int adminId) {
        if (!dbHelper.isProductOwner(productId, adminId)) {
            Log.w("ProductDAO", "Admin " + adminId + " does not have permission to delete product " + productId);
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PRODUCT, "product_id = ?", new String[]{String.valueOf(productId)});
        db.close();
        return rowsAffected > 0;
    }

    public List<ProductImage> getProductImages(int productId) {
        List<ProductImage> imageList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT_IMAGES + " WHERE product_id = ?",
                new String[]{String.valueOf(productId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ProductImage image = new ProductImage();
                image.setImageId(cursor.getInt(cursor.getColumnIndexOrThrow("image_id")));
                image.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                image.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                imageList.add(image);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return imageList;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow("created_by")));
                productList.add(product);
                Log.d("ProductDAO", "Product ID: " + product.getProductId() + ", Image URL: " + product.getImageUrl());
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return productList;
    }

    public List<Product> getProductsByAdmin(int adminId) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + " WHERE created_by = ?",
                new String[]{String.valueOf(adminId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow("created_by")));
                productList.add(product);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return productList;
    }

    public void updateProductImages(int productId, List<Uri> imageUris) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_PRODUCT_IMAGES, "product_id = ?", new String[]{String.valueOf(productId)});
        for (Uri uri : imageUris) {
            ContentValues values = new ContentValues();
            values.put("product_id", productId);
            values.put("image_url", uri.toString());
            db.insert(TABLE_PRODUCT_IMAGES, null, values);
        }
        db.close();
    }

    public Product getProductById(int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Product product = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + " WHERE product_id = ?",
                    new String[]{String.valueOf(productId)});

            if (cursor != null && cursor.moveToFirst()) {
                product = new Product();
                int productIdIndex = cursor.getColumnIndex("product_id");
                int productNameIndex = cursor.getColumnIndex("product_name");
                int priceIndex = cursor.getColumnIndex("price");
                int imageUrlIndex = cursor.getColumnIndex("image_url");
                int descriptionIndex = cursor.getColumnIndex("description");
                int createdByIndex = cursor.getColumnIndex("created_by");

                if (productIdIndex != -1 && productNameIndex != -1 && priceIndex != -1 &&
                        imageUrlIndex != -1 && descriptionIndex != -1 && createdByIndex != -1) {
                    product.setProductId(cursor.getInt(productIdIndex));
                    product.setProductName(cursor.getString(productNameIndex));
                    product.setPrice(cursor.getDouble(priceIndex));
                    product.setImageUrl(cursor.getString(imageUrlIndex));
                    product.setDescription(cursor.getString(descriptionIndex));
                    product.setCreatedBy(cursor.getInt(createdByIndex));
                } else {
                    Log.e("ProductDAO", "Một hoặc nhiều cột không tồn tại trong bảng product");
                }
            } else {
                Log.w("ProductDAO", "Không tìm thấy sản phẩm với ID: " + productId);
            }
        } catch (Exception e) {
            Log.e("ProductDAO", "Lỗi khi lấy sản phẩm theo ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        db.close();
        return product;
    }
}