package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AdminSeeder {
    private final DatabaseHelper dbHelper;

    public AdminSeeder(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void seedAdminAccount() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!isAdminExists(db, "quzkhanh.dev@gmail.com")) {
            // Thêm tài khoản admin mặc định
            ContentValues userValues = new ContentValues();
            userValues.put("fullName", "Quoc Khanh");
            userValues.put("email", "quzkhanh.dev@gmail.com");
            userValues.put("password", "khanh123"); // Mã hóa nếu cần
            userValues.put("phoneNumber", "0866206193");
            db.insert("users", null, userValues);

            // Thêm admin vào bảng admin
            ContentValues adminValues = new ContentValues();
            adminValues.put("email", "quzkhanh.dev@gmail.com");
            db.insert("admin", null, adminValues);

            Log.d("AdminSeeder", "Admin account created successfully.");
        } else {
            Log.d("AdminSeeder", "Admin account already exists.");
        }

        db.close();
    }

    private boolean isAdminExists(SQLiteDatabase db, String email) {
        Cursor cursor = db.rawQuery("SELECT * FROM admin WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Đóng cursor để tránh rò rỉ bộ nhớ
        return exists;
    }
}
