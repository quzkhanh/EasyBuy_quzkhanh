package com.example.easybuy.Database;

import android.content.Context;

import com.example.easybuy.Model.Admin;

public class AdminDAO {
    private DatabaseHelper dbHelper;

    public AdminDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addAdmin(Admin admin) {
        return dbHelper.addAdmin(admin);
    }

    public Admin getAdminByEmail(String email) {
        return dbHelper.getAdminByEmailAndPassword(email, null); // Chỉ kiểm tra email
    }

    public Admin getAdminByEmailAndPassword(String email, String password) {
        return dbHelper.getAdminByEmailAndPassword(email, password);
    }

    public boolean checkAdminEmail(String email) {
        return dbHelper.checkAdminEmail(email);
    }

    public boolean updateAdmin(Admin admin) {
        return dbHelper.updateAdmin(admin);
    }

    public boolean deleteAdmin(int adminId) {
        return dbHelper.deleteAdmin(adminId);
    }

    // Không cần open() hay close() vì DatabaseHelper tự quản lý
}