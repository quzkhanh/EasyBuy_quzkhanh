package com.example.easybuy.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ADMIN_ID = "admin_id";
    private static final String KEY_USER_ID = "user_id"; // Thêm key cho userId
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMAIL = "email";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Đổi tên createLoginSession thành createAdminLoginSession để rõ ràng
    public void createAdminLoginSession(int adminId, String email, String fullName) {
        editor.putInt(KEY_ADMIN_ID, adminId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putInt(KEY_USER_ID, -1); // Đặt userId thành -1 khi đăng nhập admin
        editor.apply();
    }

    // Thêm phương thức cho user
    public void createUserLoginSession(int userId, String email, String fullName) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putInt(KEY_ADMIN_ID, -1); // Đặt adminId thành -1 khi đăng nhập user
        editor.apply();
    }

    public int getAdminId() {
        return pref.getInt(KEY_ADMIN_ID, -1);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    // Đổi tên getAdminEmail thành getEmail để phù hợp cho cả admin và user
    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    // Đổi tên setAdminEmail thành setEmail để phù hợp cho cả admin và user
    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getInt(KEY_ADMIN_ID, -1) != -1 || pref.getInt(KEY_USER_ID, -1) != -1;
    }
}