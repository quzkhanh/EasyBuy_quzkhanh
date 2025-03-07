package com.example.easybuy.Database.DAO;

import android.content.Context;

import com.example.easybuy.Database.DatabaseHelper.DatabaseHelper;
import com.example.easybuy.Database.DatabaseHelper.UserDatabaseHelper;
import com.example.easybuy.Model.User;

import java.util.List;

public class UserDAO {
    private final UserDatabaseHelper userDbHelper;

    public UserDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.userDbHelper = new UserDatabaseHelper(dbHelper); // Khởi tạo UserDatabaseHelper
    }

    // Thêm người dùng mới
    public long addUser(User user) {
        return userDbHelper.addUser(user);
    }

    // Lấy danh sách tất cả người dùng
    public List<User> getAllUsers() {
        return userDbHelper.getAllUsers();
    }

    // Tìm kiếm người dùng theo ID
    public User getUserById(int id) {
        return userDbHelper.getUserById(id);
    }

    // Cập nhật thông tin người dùng
    public int updateUser(User user) {
        return userDbHelper.updateUser(user);
    }

    // Xóa người dùng
    public int deleteUser(int userId) {
        return userDbHelper.deleteUser(userId);
    }

    // Kiểm tra đăng nhập
    public User checkLogin(String email, String password) {
        return userDbHelper.checkLogin(email, password);
    }

    // Lấy người dùng theo email
    public User getUserByEmail(String email) {
        return userDbHelper.getUserByEmail(email);
    }

    // Kiểm tra email đã tồn tại chưa
    public boolean isEmailExists(String email) {
        return userDbHelper.checkEmail(email);
    }

    // Thêm phương thức close()
    public void close() {
        if (userDbHelper != null) {
            userDbHelper.close(); // Gọi close() trên UserDatabaseHelper
        }
    }
}