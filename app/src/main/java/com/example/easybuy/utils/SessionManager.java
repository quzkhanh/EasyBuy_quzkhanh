package com.example.easybuy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ADMIN_ID = "admin_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMAIL = "email";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private int tempAdminId = -1; // Biến tạm để lưu adminId khi không lưu phiên
    private int tempUserId = -1;  // Biến tạm để lưu userId khi không lưu phiên

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createAdminLoginSession(int adminId, String email, String fullName) {
        editor.putInt(KEY_ADMIN_ID, adminId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putInt(KEY_USER_ID, -1);
        editor.apply();
        tempAdminId = adminId; // Lưu tạm thời
    }

    public void createUserLoginSession(int userId, String email, String fullName) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putInt(KEY_ADMIN_ID, -1);
        editor.apply();
        tempUserId = userId; // Lưu tạm thời
    }

    public int getAdminId() {
        int savedAdminId = pref.getInt(KEY_ADMIN_ID, -1);
        return savedAdminId != -1 ? savedAdminId : tempAdminId; // Ưu tiên SharedPreferences
    }

    public int getUserId() {
        int savedUserId = pref.getInt(KEY_USER_ID, -1);
        return savedUserId != -1 ? savedUserId : tempUserId; // Ưu tiên SharedPreferences
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
        tempAdminId = -1; // Xóa tạm thời
        tempUserId = -1;
    }

    public boolean isLoggedIn() {
        int userId = getUserId();
        int adminId = getAdminId();
        return userId != -1 || adminId != -1;
    }

    public boolean isAdmin() {
        return getAdminId() != -1;
    }
}