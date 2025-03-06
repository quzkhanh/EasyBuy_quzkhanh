package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;
import com.example.easybuy.Model.Admin;

public class AdminDAO {
    private DatabaseHelper dbHelper;

    public AdminDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long addAdmin(Admin admin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", admin.getFullName());
        values.put("email", admin.getEmail());
        // Hash mật khẩu trước khi lưu
        String hashedPassword = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
        values.put("password", hashedPassword);
        values.put("role", 1);
        long id = db.insert(DatabaseHelper.TABLE_ADMINS, null, values);
        db.close();
        return id;
    }

    public boolean checkAdminLogin(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;
        try {
            cursor = db.rawQuery("SELECT password FROM " + DatabaseHelper.TABLE_ADMINS + " WHERE email = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                String storedHashedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                isValid = BCrypt.checkpw(password, storedHashedPassword);
            }
        } catch (Exception e) {
            Log.e("AdminDAO", "Error in checkAdminLogin: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return isValid;
    }

    public boolean checkAdminEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.rawQuery("SELECT 1 FROM " + DatabaseHelper.TABLE_ADMINS + " WHERE email = ? LIMIT 1", new String[]{email});
            exists = cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("AdminDAO", "Error in checkAdminEmail: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return exists;
    }

    public boolean updateAdmin(Admin admin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (admin.getFullName() != null) values.put("full_name", admin.getFullName());
        if (admin.getEmail() != null) values.put("email", admin.getEmail());
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            values.put("password", BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()));
        }
        int rowsAffected = db.update(DatabaseHelper.TABLE_ADMINS, values, "id = ?", new String[]{String.valueOf(admin.getId())});
        db.close();
        return rowsAffected > 0;
    }

    public Admin getAdminByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Admin admin = null;
        try {
            cursor = db.rawQuery("SELECT id, full_name, email, password FROM " + DatabaseHelper.TABLE_ADMINS + " WHERE email = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                admin = new Admin(id, fullName, email, storedPassword);
            }
        } catch (Exception e) {
            Log.e("AdminDAO", "Error in getAdminByEmail: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return admin;
    }

    public boolean deleteAdmin(int adminId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_ADMINS, "id = ?", new String[]{String.valueOf(adminId)});
        db.close();
        return rowsAffected > 0;
    }
}