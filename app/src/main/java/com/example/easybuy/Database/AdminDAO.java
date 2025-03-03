package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.Models.Admin;

public class AdminDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public AdminDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm admin mới vào database
    public long addAdmin(Admin admin) {
        ContentValues values = new ContentValues();
        values.put("full_name", admin.getFullName());
        values.put("email", admin.getEmail());
        values.put("password", admin.getPassword());

        return database.insert("admins", null, values);
    }

    // Kiểm tra xem email đã tồn tại chưa
    public Admin getAdminByEmail(String email) {
        Cursor cursor = database.query("admins", null, "email=?", new String[]{email},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Admin admin = new Admin(
                    cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
            cursor.close();
            return admin;
        }
        return null;
    }

    // Kiểm tra đăng nhập Admin

    public boolean authenticateAdmin(String email, String password) {
        Cursor cursor = database.query("admins", new String[]{"email"},
                "email = ? AND password = ? AND role = ?", new String[]{email, password, "1"},
                null, null, null);

        boolean isAuthenticated = cursor.getCount() > 0;
        cursor.close();
        return isAuthenticated;
    }



}
