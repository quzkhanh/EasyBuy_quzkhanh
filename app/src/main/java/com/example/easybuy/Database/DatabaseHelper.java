package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.easybuy.Model.Admin;

import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "easybuy.db";
    private static final int DATABASE_VERSION = 11;
    private static final String TABLE_OTP = "otp_table";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ADMINS = "admins";
    private static final String TABLE_PRODUCT = "product";
    private static final String TABLE_PRODUCT_IMAGES = "product_images";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ADMINS_TABLE);
        db.execSQL(CREATE_OTP_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_PRODUCT_IMAGES_TABLE);

        db.execSQL("INSERT INTO " + TABLE_USERS + " (email, password) VALUES ('seller@easybuy.com', 'seller123')");
        db.execSQL("INSERT INTO " + TABLE_ADMINS + " (email, password, full_name) VALUES ('admin@easybuy.com', 'admin123', 'Admin User')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);

        try {
            if (oldVersion < 11) {
                db.execSQL("ALTER TABLE " + TABLE_PRODUCT + " ADD COLUMN created_by INTEGER");
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
                Log.d("DatabaseHelper", "Added created_by column to product table");
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

    // --- Các phương thức cho User ---
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
        values.put("password", newPassword);
        int rowsAffected = db.update(TABLE_USERS, values, "email = ?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    // --- Các phương thức cho Admin ---
    // Thêm admin mới
    public long addAdmin(Admin admin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", admin.getFullName());
        values.put("email", admin.getEmail());
        values.put("password", admin.getPassword());
        values.put("role", 1); // Role mặc định cho admin

        long id = db.insert(TABLE_ADMINS, null, values);
        db.close();
        return id; // Trả về id của admin vừa thêm, hoặc -1 nếu lỗi
    }

    // Kiểm tra đăng nhập admin
    public Admin getAdminByEmailAndPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ADMINS + " WHERE email = ? AND password = ?",
                new String[]{email, password});
        Admin admin = null;
        if (cursor.moveToFirst()) {
            admin = new Admin(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
        }
        cursor.close();
        db.close();
        return admin; // Trả về null nếu không tìm thấy
    }

    // Kiểm tra email admin đã tồn tại chưa
    public boolean checkAdminEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ADMINS + " WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Cập nhật thông tin admin
    public boolean updateAdmin(Admin admin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", admin.getFullName());
        values.put("email", admin.getEmail());
        values.put("password", admin.getPassword());
        int rowsAffected = db.update(TABLE_ADMINS, values, "id = ?", new String[]{String.valueOf(admin.getId())});
        db.close();
        return rowsAffected > 0;
    }

    // Xóa admin
    public boolean deleteAdmin(int adminId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_ADMINS, "id = ?", new String[]{String.valueOf(adminId)});
        db.close();
        return rowsAffected > 0;
    }

    // --- Các phương thức cho Product ---
    public boolean isProductOwner(int productId, int adminId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT + " WHERE product_id = ? AND created_by = ?",
                new String[]{String.valueOf(productId), String.valueOf(adminId)});
        boolean isOwner = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isOwner;
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
}