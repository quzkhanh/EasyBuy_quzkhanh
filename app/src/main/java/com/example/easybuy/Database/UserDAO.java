package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.Models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Mở kết nối database
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Đóng kết nối database
    public void close() {
        dbHelper.close();
    }

    // Thêm người dùng mới
    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("phoneNumber", user.getPhoneNumber());

        return database.insert("users", null, values);
    }

    // Lấy danh sách tất cả người dùng
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM users", null);

        if (cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(0);
                String fullName = cursor.getString(1);
                String email = cursor.getString(2);
                String password = cursor.getString(3);
                String phoneNumber = cursor.getString(4);

                userList.add(new User(userId, fullName, email, password, phoneNumber));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    // Tìm kiếm người dùng theo ID
    public User getUserById(int id) {
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE userId = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            return user;
        }
        return null;
    }

    // Cập nhật thông tin người dùng
    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("phoneNumber", user.getPhoneNumber());

        return database.update("users", values, "userId = ?", new String[]{String.valueOf(user.getUserId())});
    }

    // Xóa người dùng
    public int deleteUser(int userId) {
        return database.delete("users", "userId = ?", new String[]{String.valueOf(userId)});
    }

    // Kiểm tra đăng nhập
    public User checkLogin(String email, String password) {
        Cursor cursor = database.query("users", null,
                "email = ? AND password = ? AND role = ?",
                new String[]{email, password, "0"},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
            );
            cursor.close();
            return user;
        }

        return null; // Không tìm thấy user
    }


    public User getUserByEmail(String email) {
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            return user;
        }
        return null; // Email chưa tồn tại
    }


}
