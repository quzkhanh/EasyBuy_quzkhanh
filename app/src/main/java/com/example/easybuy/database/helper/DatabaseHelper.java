package com.example.easybuy.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "easybuy.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_OTP = "otp_table";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ADMINS = "admins";
    public static final String TABLE_PRODUCT = "product";
    public static final String TABLE_PRODUCT_IMAGES = "product_images";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_FAVORITES = "favorites";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstants.CREATE_USERS_TABLE);
        db.execSQL(DatabaseConstants.CREATE_ADMINS_TABLE);
        db.execSQL(DatabaseConstants.CREATE_OTP_TABLE);
        db.execSQL(DatabaseConstants.CREATE_PRODUCT_TABLE);
        db.execSQL(DatabaseConstants.CREATE_PRODUCT_IMAGES_TABLE);
        db.execSQL(DatabaseConstants.CREATE_ORDERS_TABLE);
        db.execSQL(DatabaseConstants.CREATE_FAVORITES_TABLE);

        String userHashedPassword = BCrypt.hashpw("seller123", BCrypt.gensalt());
        db.execSQL("INSERT INTO " + TABLE_USERS + " (email, password) VALUES ('seller@easybuy.com', ?)",
                new Object[]{userHashedPassword});

        String adminHashedPassword = BCrypt.hashpw("12345", BCrypt.gensalt());
        db.execSQL("INSERT INTO " + TABLE_ADMINS + " (email, password, full_name, role) VALUES ('admin@easybuy.com', ?, 'Admin User', 1)",
                new Object[]{adminHashedPassword});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OTP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);
        onCreate(db);
    }
}