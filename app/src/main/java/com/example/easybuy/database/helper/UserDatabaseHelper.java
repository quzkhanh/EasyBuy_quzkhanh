package com.example.easybuy.database.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class UserDatabaseHelper {
    private final DatabaseHelper dbHelper;

    public UserDatabaseHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Existing methods remain unchanged...
    public SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase();
    }

    public long addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("email", user.getEmail());
        values.put("password", BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        values.put("phoneNumber", user.getPhoneNumber());
        values.put("role", 0);
        long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        return result;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userId, fullName, email, password, phoneNumber FROM " +
                DatabaseHelper.TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                User user = extractUserFromCursor(cursor);
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userId, fullName, email, password, phoneNumber FROM " +
                DatabaseHelper.TABLE_USERS + " WHERE userId = ?", new String[]{String.valueOf(id)});
        User user = null;
        if (cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor);
        }
        cursor.close();
        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullName", user.getFullName());
        values.put("email", user.getEmail());
        values.put("password", BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        values.put("phoneNumber", user.getPhoneNumber());
        int result = db.update(DatabaseHelper.TABLE_USERS, values, "userId = ?",
                new String[]{String.valueOf(user.getUserId())});
        return result;
    }

    public int deleteUser(int userId) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(DatabaseHelper.TABLE_USERS, "userId = ?",
                new String[]{String.valueOf(userId)});
        return result;
    }

    public User checkLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userId, fullName, email, password, phoneNumber FROM " +
                DatabaseHelper.TABLE_USERS + " WHERE email = ? AND role = 0", new String[]{email});
        User user = null;
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            if (BCrypt.checkpw(password, storedPassword)) {
                user = extractUserFromCursor(cursor);
            }
        }
        cursor.close();
        return user;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userId, fullName, email, password, phoneNumber FROM " +
                DatabaseHelper.TABLE_USERS + " WHERE email = ?", new String[]{email});
        User user = null;
        if (cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor);
        }
        cursor.close();
        return user;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userId FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updatePhoneNumber(int userId, String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phoneNumber", phoneNumber);
        int rowsAffected = db.update(DatabaseHelper.TABLE_USERS, values, "userId = ?",
                new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public List<User> getUsersByRole(int role) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userId, fullName, email, password, phoneNumber FROM " +
                DatabaseHelper.TABLE_USERS + " WHERE role = ?", new String[]{String.valueOf(role)});

        if (cursor.moveToFirst()) {
            do {
                User user = extractUserFromCursor(cursor);
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    // New OTP-related methods
    public boolean saveOTP(int userId, String otp) {
        SQLiteDatabase db = getWritableDatabase();
        String hashedOTP = BCrypt.hashpw(otp, BCrypt.gensalt());

        ContentValues values = new ContentValues();
        values.put("otp", hashedOTP);
        values.put("otp_timestamp", System.currentTimeMillis());

        int rowsAffected = db.update(DatabaseHelper.TABLE_USERS, values, "userId = ?",
                new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public boolean verifyOTP(String email, String otp) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userId, otp, otp_timestamp FROM " +
                        DatabaseHelper.TABLE_USERS + " WHERE email = ?",
                new String[]{email});

        boolean isValid = false;
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
            String storedOTP = cursor.getString(cursor.getColumnIndexOrThrow("otp"));
            long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("otp_timestamp"));

            long currentTime = System.currentTimeMillis();
            long fiveMinutes = 5 * 60 * 1000; // Hiệu lực 5 phút

            if (storedOTP != null && BCrypt.checkpw(otp, storedOTP) &&
                    (currentTime - timestamp) <= fiveMinutes) {
                isValid = true;
                clearOTP(userId);
            }
        }

        cursor.close();
        return isValid;
    }

    private void clearOTP(int userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull("otp");
        values.putNull("otp_timestamp");
        db.update(DatabaseHelper.TABLE_USERS, values, "userId = ?",
                new String[]{String.valueOf(userId)});
    }

    private User extractUserFromCursor(Cursor cursor) {
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
        );
    }
    public boolean generateAndSaveOTP(String email) {
        // Tạo OTP ngẫu nhiên 6 chữ số
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        SQLiteDatabase db = getWritableDatabase();
        String hashedOTP = BCrypt.hashpw(otp, BCrypt.gensalt());

        ContentValues values = new ContentValues();
        values.put("otp", hashedOTP);
        values.put("otp_timestamp", System.currentTimeMillis());

        int rowsAffected = db.update(DatabaseHelper.TABLE_USERS, values, "email = ?",
                new String[]{email});

        return rowsAffected > 0;
    }

    // Thêm phương thức close() ở đây
    public void close() {
        dbHelper.close(); // Gọi close() trên DatabaseHelper
    }
}