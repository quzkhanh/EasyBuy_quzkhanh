package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.easybuy.Model.Admin;
import com.example.easybuy.Model.Order;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "easybuy.db";
    private static final int DATABASE_VERSION = 4; // Giữ nguyên hoặc tăng lên nếu cần
    public static final String TABLE_OTP = "otp_table";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ADMINS = "admins";
    public static final String TABLE_PRODUCT = "product";
    public static final String TABLE_PRODUCT_IMAGES = "product_images";
    public static final String TABLE_ORDERS = "orders";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Tables SQL
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fullName TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "phoneNumber TEXT, " +
                "role INTEGER DEFAULT 0)";

        String CREATE_ADMINS_TABLE = "CREATE TABLE " + TABLE_ADMINS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role INTEGER DEFAULT 1)";

        String CREATE_OTP_TABLE = "CREATE TABLE " + TABLE_OTP + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL, " +
                "otp TEXT NOT NULL, " +
                "expiry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + " (" +
                "product_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_name TEXT NOT NULL, " +
                "price REAL CHECK (price >= 0), " +
                "image_url TEXT, " +
                "description TEXT, " +
                "created_by INTEGER NOT NULL, " +
                "FOREIGN KEY (created_by) REFERENCES " + TABLE_ADMINS + "(id) ON DELETE SET NULL)";

        String CREATE_PRODUCT_IMAGES_TABLE = "CREATE TABLE " + TABLE_PRODUCT_IMAGES + " (" +
                "image_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_id INTEGER NOT NULL, " +
                "image_url TEXT NOT NULL, " +
                "FOREIGN KEY (product_id) REFERENCES " + TABLE_PRODUCT + "(product_id) ON DELETE CASCADE)";

        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + " (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "total_price REAL NOT NULL, " +
                "status TEXT NOT NULL, " +
                "order_date TEXT NOT NULL, " +
                "shipping_address TEXT, " +
                "phone_number TEXT, " +
                "payment_method TEXT, " +
                "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(userId) ON DELETE CASCADE, " +
                "FOREIGN KEY (product_id) REFERENCES " + TABLE_PRODUCT + "(product_id) ON DELETE CASCADE)";

        // Execute table creation
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ADMINS_TABLE);
        db.execSQL(CREATE_OTP_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_PRODUCT_IMAGES_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);

        // Insert default users với mật khẩu đã hash
        String userHashedPassword = BCrypt.hashpw("seller123", BCrypt.gensalt());
        db.execSQL("INSERT INTO " + TABLE_USERS + " (email, password) VALUES ('seller@easybuy.com', '" + userHashedPassword + "')");

        // Thêm dữ liệu mẫu với mật khẩu đã hash cho admin
        String adminHashedPassword = BCrypt.hashpw("12345", BCrypt.gensalt());
        db.execSQL("INSERT INTO " + TABLE_ADMINS + " (email, password, full_name, role) VALUES ('admin@easybuy.com', '" + adminHashedPassword + "', 'Admin User', 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);

        try {
            // Chỉ thực hiện nâng cấp nếu cần thiết
            if (oldVersion < 11) {
                // Bỏ qua lệnh ALTER TABLE vì cột created_by đã có trong onCreate
                // db.execSQL("ALTER TABLE " + TABLE_PRODUCT + " ADD COLUMN created_by INTEGER"); // Comment hoặc xóa dòng này
                db.execSQL("UPDATE " + TABLE_PRODUCT + " SET created_by = 1 WHERE created_by IS NULL");
                db.execSQL("PRAGMA foreign_keys=off");
                db.execSQL("CREATE TABLE temp_product AS SELECT * FROM " + TABLE_PRODUCT);
                db.execSQL("DROP TABLE " + TABLE_PRODUCT);
                db.execSQL("CREATE TABLE " + TABLE_PRODUCT + " (" +
                        "product_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "product_name TEXT NOT NULL, " +
                        "price REAL CHECK (price >= 0), " +
                        "image_url TEXT, " +
                        "description TEXT, " +
                        "created_by INTEGER NOT NULL, " +
                        "FOREIGN KEY (created_by) REFERENCES " + TABLE_ADMINS + "(id) ON DELETE SET NULL)");
                db.execSQL("INSERT INTO " + TABLE_PRODUCT + " SELECT * FROM temp_product");
                db.execSQL("DROP TABLE temp_product");
                db.execSQL("PRAGMA foreign_keys=on");
            }

            if (oldVersion < 2) {
                db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                        "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER NOT NULL, " +
                        "total_price REAL NOT NULL, " +
                        "status TEXT NOT NULL, " +
                        "order_date TEXT NOT NULL, " +
                        "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(userId) ON DELETE CASCADE)");
                Log.d("DatabaseHelper", "Created initial orders table");
            }

            if (oldVersion < 13) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
                db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                        "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER NOT NULL, " +
                        "product_id INTEGER NOT NULL, " +
                        "quantity INTEGER NOT NULL, " +
                        "total_price REAL NOT NULL, " +
                        "status TEXT NOT NULL, " +
                        "order_date TEXT NOT NULL, " +
                        "shipping_address TEXT, " +
                        "phone_number TEXT, " +
                        "payment_method TEXT, " +
                        "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(userId) ON DELETE CASCADE, " +
                        "FOREIGN KEY (product_id) REFERENCES " + TABLE_PRODUCT + "(product_id) ON DELETE CASCADE)");
                Log.d("DatabaseHelper", "Upgraded orders table to version 13");
            }

            backupProductTable(db);
            db.execSQL("DELETE FROM " + TABLE_PRODUCT_IMAGES);
            db.execSQL("DELETE FROM " + TABLE_PRODUCT);
            db.execSQL("DELETE FROM " + TABLE_OTP);
            db.execSQL("DELETE FROM " + TABLE_USERS);
            db.execSQL("DELETE FROM " + TABLE_ADMINS);
            Log.d("DatabaseHelper", "Data deleted from all tables");

            onCreate(db);
            Log.d("DatabaseHelper", "Database upgraded successfully");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error during upgrade: " + e.getMessage());
            throw e;
        }
    }

    private void backupProductTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS product_backup AS SELECT * FROM " + TABLE_PRODUCT + " WITH NO DATA");
        db.execSQL("INSERT INTO product_backup SELECT * FROM " + TABLE_PRODUCT);
        Log.d("DatabaseHelper", "Product table backed up to product_backup");
    }

    // *** PRODUCT METHODS ***
    public boolean isProductOwner(int productId, int adminId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + " WHERE product_id = ? AND created_by = ?",
                    new String[]{String.valueOf(productId), String.valueOf(adminId)});
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking product owner", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public long addProduct(String productName, double price, String imageUrl, String description, int adminId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_name", productName);
        values.put("price", price);
        values.put("image_url", imageUrl);
        values.put("description", description);
        values.put("created_by", adminId);
        long id = db.insert(TABLE_PRODUCT, null, values);
        db.close();
        return id;
    }

    // *** OTP AND USER METHODS ***
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public String generateAndSaveOTP(String email) {
        Random random = new Random();
        String otp = String.format("%05d", random.nextInt(100000));
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("otp", otp);
        db.insert(TABLE_OTP, null, values);
        db.close();
        return otp;
    }

    public boolean verifyOTP(String email, String otp) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_OTP + " WHERE email = ? AND otp = ?",
                new String[]{email, otp});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        if (isValid) {
            db.delete(TABLE_OTP, "email = ? AND otp = ?", new String[]{email, otp});
        }
        db.close();
        return isValid;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", BCrypt.hashpw(newPassword, BCrypt.gensalt())); // Hash mật khẩu mới
        int rowsAffected = db.update(TABLE_USERS, values, "email = ?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    public long addOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("product_id", order.getProductId());
        values.put("quantity", order.getQuantity());
        values.put("total_price", order.getTotalPrice());
        values.put("status", order.getStatus());
        values.put("order_date", order.getOrderDate());
        values.put("shipping_address", order.getShippingAddress());
        values.put("phone_number", order.getPhoneNumber());
        values.put("payment_method", order.getPaymentMethod());
        long id = db.insert(TABLE_ORDERS, null, values);
        db.close();
        return id;
    }
}