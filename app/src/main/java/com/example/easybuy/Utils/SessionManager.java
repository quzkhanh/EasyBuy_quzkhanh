package com.example.easybuy.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ADMIN_ID = "admin_id";
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

    public void createLoginSession(int adminId, String email, String fullName) {
        editor.putInt(KEY_ADMIN_ID, adminId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_NAME, fullName);
        editor.apply();
    }

    public int getAdminId() {
        return pref.getInt(KEY_ADMIN_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    public String getAdminEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void setAdminEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getInt(KEY_ADMIN_ID, -1) != -1;
    }
}