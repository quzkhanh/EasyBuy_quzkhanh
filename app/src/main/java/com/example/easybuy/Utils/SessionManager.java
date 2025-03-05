package com.example.easybuy.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AdminSession";
    private static final String KEY_ADMIN_ID = "adminId";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Lưu thông tin khi admin đăng nhập
    public void createLoginSession(int adminId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_ADMIN_ID, adminId);
        editor.apply();
    }

    // Lấy adminId
    public int getAdminId() {
        return prefs.getInt(KEY_ADMIN_ID, -1); // -1 nếu chưa đăng nhập
    }

    // Kiểm tra xem admin đã đăng nhập chưa
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Đăng xuất
    public void logout() {
        editor.clear();
        editor.apply();
    }
}