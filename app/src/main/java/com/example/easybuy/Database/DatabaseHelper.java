package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "easybuy.db";
    private static final int DATABASE_VERSION = 4; // Cập nhật version để force update database
    private static final String TABLE_OTP = "otp_table";
    private static final String TABLE_USERS = "users"; // Thêm hằng số cho bảng users
    private static final String TABLE_ADMINS = "admins"; // Thêm hằng số cho bảng admins

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fullName TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "phoneNumber TEXT, " +
                "role INTEGER DEFAULT 0)"; // 0: User

        // Tạo bảng admins
        String CREATE_ADMIN_TABLE = "CREATE TABLE " + TABLE_ADMINS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role INTEGER DEFAULT 1)"; // 1: Admin

        // Tạo bảng otp_table
        String CREATE_OTP_TABLE = "CREATE TABLE " + TABLE_OTP + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL, " +
                "otp TEXT NOT NULL, " +
                "expiry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ADMIN_TABLE);
        db.execSQL(CREATE_OTP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OTP);
        onCreate(db);
    }

    // Kiểm tra email tồn tại (trong bảng users)
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Tạo và lưu OTP
    public String generateAndSaveOTP(String email) {
        Random random = new Random();
        String otp = String.format("%05d", random.nextInt(100000)); // Tạo mã 5 chữ số
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("otp", otp);
        db.insert(TABLE_OTP, null, values);
        return otp;
    }

    // Xác minh OTP
    public boolean verifyOTP(String email, String otp) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_OTP + " WHERE email = ? AND otp = ?",
                new String[]{email, otp});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        // Xóa OTP sau khi xác minh (tùy chọn)
        if (isValid) {
            db.delete(TABLE_OTP, "email = ? AND otp = ?", new String[]{email, otp});
        }
        return isValid;
    }

    // Cập nhật mật khẩu trong bảng users
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rowsAffected = db.update(TABLE_USERS, values, "email = ?", new String[]{email});
        return rowsAffected > 0;
    }
}