package com.example.easybuy.database.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDatabaseHelper {
    private final DatabaseHelper dbHelper;

    public OrderDatabaseHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase();
    }

    public long addOrder(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("product_id", order.getProductId());
        values.put("quantity", order.getQuantity());
        values.put("total_price", order.getTotalPrice());
        values.put("status", order.getStatus());
        values.put("order_date", order.getOrderDate());
        values.put("shipping_address", order.getShippingAddress());
        values.put("phone_number", order.getPhoneNumber());
        values.put("payment_method", order.getPaymentMethod());
        return db.insert(DatabaseHelper.TABLE_ORDERS, null, values);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ORDERS, null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                order.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow("order_date")));
                order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
                order.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
                order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ORDERS + " WHERE user_id = ?",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                order.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow("order_date")));
                order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
                order.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
                order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        int rowsAffected = db.update(DatabaseHelper.TABLE_ORDERS, values, "order_id = ?",
                new String[]{String.valueOf(orderId)});
        return rowsAffected > 0;
    }

    public boolean deleteOrder(int orderId) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_ORDERS, "order_id = ?",
                new String[]{String.valueOf(orderId)});
        return rowsAffected > 0;
    }

    public List<Order> getOrdersByAdminId(int adminId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT o.* FROM " + DatabaseHelper.TABLE_ORDERS + " o " +
                "INNER JOIN " + DatabaseHelper.TABLE_PRODUCT + " p ON o.product_id = p.product_id " +
                "WHERE p.created_by = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(adminId)});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow("order_id")));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                order.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow("order_date")));
                order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
                order.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
                order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }
}