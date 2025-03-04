package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.Model.Product;
import com.example.easybuy.Model.ProductImage;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private DatabaseHelper dbHelper;
    private Context context;  // ✅ Thêm biến context

    private static final String TABLE_PRODUCT = "product";
    private static final String TABLE_PRODUCT_IMAGES = "product_images";

    // ✅ Constructor duy nhất, không trùng lặp
    public ProductDAO(Context context) {
        this.context = context;  // ✅ Lưu context
        dbHelper = new DatabaseHelper(context);
    }

    // ✅ Thêm phương thức getContext()
    public Context getContext() {
        return context;
    }
    // Thêm sản phẩm kèm nhiều ảnh
    public long addProductWithImages(Product product, List<String> additionalImageUrls) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", product.getProductName());
        values.put("price", product.getPrice());
        values.put("image_url", product.getImageUrl()); // Ảnh chính
        values.put("description", product.getDescription());

        // Thêm sản phẩm vào bảng product
        long productId = db.insert(TABLE_PRODUCT, null, values);

        // Thêm các ảnh phụ vào bảng product_images
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
    // ✅ Thêm sản phẩm mới
    public long addProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", product.getProductName());
        values.put("price", product.getPrice());
        values.put("image_url", product.getImageUrl());
        values.put("description", product.getDescription());
        return db.insert(TABLE_PRODUCT, null, values);
    }

    // ✅ Thêm ảnh mô tả cho sản phẩm
    public long addProductImage(int productId, String imageUrl) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("image_url", imageUrl);
        return db.insert(TABLE_PRODUCT_IMAGES, null, values);
    }

    // ✅ Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }

    // ✅ Lấy danh sách ảnh của một sản phẩm
    public List<ProductImage> getProductImages(int productId) {
        List<ProductImage> imageList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT_IMAGES + " WHERE product_id = ?",
                new String[]{String.valueOf(productId)});

        if (cursor.moveToFirst()) {
            do {
                ProductImage image = new ProductImage();
                image.setImageId(cursor.getInt(cursor.getColumnIndexOrThrow("image_id")));
                image.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                image.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                imageList.add(image);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return imageList;
    }

    // ✅ Cập nhật sản phẩm
    public int updateProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", product.getProductName());
        values.put("price", product.getPrice());
        values.put("image_url", product.getImageUrl());
        values.put("description", product.getDescription());
        return db.update(TABLE_PRODUCT, values, "product_id = ?", new String[]{String.valueOf(product.getProductId())});
    }

    // ✅ Xóa sản phẩm (sẽ xóa cả ảnh nhờ ON DELETE CASCADE)
    public void deleteProduct(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_PRODUCT, "product_id = ?", new String[]{String.valueOf(productId)});
    }
}
