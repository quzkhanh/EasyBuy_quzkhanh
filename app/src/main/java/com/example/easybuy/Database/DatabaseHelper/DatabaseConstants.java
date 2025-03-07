package com.example.easybuy.Database.DatabaseHelper;

public interface DatabaseConstants {

    String CREATE_USERS_TABLE = "CREATE TABLE " + DatabaseHelper.TABLE_USERS + "("
            + "userId INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "fullName TEXT,"
            + "email TEXT UNIQUE,"
            + "password TEXT,"
            + "phoneNumber TEXT,"
            + "role INTEGER,"
            + "otp TEXT,"
            + "otp_timestamp LONG"
            + ")";

    String CREATE_ADMINS_TABLE = "CREATE TABLE " + DatabaseHelper.TABLE_ADMINS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "full_name TEXT NOT NULL, " +
            "email TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL, " +
            "role INTEGER DEFAULT 1)";

    String CREATE_OTP_TABLE = "CREATE TABLE " + DatabaseHelper.TABLE_OTP + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "email TEXT NOT NULL, " +
            "otp TEXT NOT NULL, " +
            "expiry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    String CREATE_PRODUCT_TABLE = "CREATE TABLE " + DatabaseHelper.TABLE_PRODUCT + " (" +
            "product_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "product_name TEXT NOT NULL, " +
            "price REAL CHECK (price >= 0), " +
            "image_url TEXT, " +
            "description TEXT, " +
            "created_by INTEGER NOT NULL, " +
            "FOREIGN KEY (created_by) REFERENCES " + DatabaseHelper.TABLE_ADMINS + "(id) ON DELETE SET NULL)";

    String CREATE_PRODUCT_IMAGES_TABLE = "CREATE TABLE " + DatabaseHelper.TABLE_PRODUCT_IMAGES + " (" +
            "image_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "product_id INTEGER NOT NULL, " +
            "image_url TEXT NOT NULL, " +
            "FOREIGN KEY (product_id) REFERENCES " + DatabaseHelper.TABLE_PRODUCT + "(product_id) ON DELETE CASCADE)";

    String CREATE_ORDERS_TABLE = "CREATE TABLE " + DatabaseHelper.TABLE_ORDERS + " (" +
            "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "product_id INTEGER NOT NULL, " +
            "quantity INTEGER NOT NULL, " +
            "total_price REAL NOT NULL, " +
            "status TEXT NOT NULL, " +
            "order_date TEXT NOT NULL, " +
            "shipping_address TEXT, " +
            "phone_number TEXT, " +
            "payment_method TEXT, " +
            "FOREIGN KEY (user_id) REFERENCES " + DatabaseHelper.TABLE_USERS + "(userId) ON DELETE CASCADE, " +
            "FOREIGN KEY (product_id) REFERENCES " + DatabaseHelper.TABLE_PRODUCT + "(product_id) ON DELETE CASCADE)";

    String CREATE_FAVORITES_TABLE = "CREATE TABLE " + DatabaseHelper.TABLE_FAVORITES + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "product_id INTEGER NOT NULL, " +
            "FOREIGN KEY (user_id) REFERENCES " + DatabaseHelper.TABLE_USERS + "(userId) ON DELETE CASCADE, " +
            "FOREIGN KEY (product_id) REFERENCES " + DatabaseHelper.TABLE_PRODUCT + "(product_id) ON DELETE CASCADE)";

}