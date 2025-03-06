package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.Model.User;

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
        if (database != null && database.isOpen()) {
            database.close();
        }
        dbHelper.close();
    }

    // Thêm người dùng mới
    public long addUser(User user) {
        open();
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("phoneNumber", user.getPhoneNumber());
        long result = database.insert("users", null, values);
        close();
        return result;
    }

    // Lấy danh sách tất cả người dùng
    public List<User> getAllUsers() {
        open();
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
        close();
        return userList;
    }

    // Tìm kiếm người dùng theo ID
    public User getUserById(int id) {
        open();
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE userId = ?", new String[]{String.valueOf(id)});
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
        }
        close();
        return user;
    }

    // Cập nhật thông tin người dùng
    public int updateUser(User user) {
        open();
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("phoneNumber", user.getPhoneNumber());
        int result = database.update("users", values, "userId = ?", new String[]{String.valueOf(user.getUserId())});
        close();
        return result;
    }

    // Xóa người dùng
    public int deleteUser(int userId) {
        open();
        int result = database.delete("users", "userId = ?", new String[]{String.valueOf(userId)});
        close();
        return result;
    }

    // Kiểm tra đăng nhập
    public User checkLogin(String email, String password) {
        open();
        Cursor cursor = database.query("users", null,
                "email = ? AND password = ? AND role = ?",
                new String[]{email, password, "0"},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
            );
            cursor.close();
        }
        close();
        return user; // Trả về null nếu không tìm thấy
    }

    public User getUserByEmail(String email) {
        open();
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
        }
        close();
        return user; // Trả về null nếu email không tồn tại
    }

    // Kiểm tra email đã tồn tại chưa
    public boolean isEmailExists(String email) {
        open();
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        close();
        return exists;
    }
}