package com.example.easybuy.Database;

import static com.example.easybuy.Database.DatabaseHelper.TABLE_PRODUCT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.Model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private DatabaseHelper dbHelper;
    private static final String TABLE_ORDERS = "orders";

    public OrderDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Thêm đơn hàng mới
    public long addOrder(Order order) {
        return dbHelper.addOrder(order);
    }

    // Lấy tất cả đơn hàng
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS, null);

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
        db.close();
        return orders;
    }

    // Lấy đơn hàng theo userId
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " WHERE user_id = ?",
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
        db.close();
        return orders;
    }

    // Cập nhật trạng thái đơn hàng
    public boolean updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        int rowsAffected = db.update(TABLE_ORDERS, values, "order_id = ?", new String[]{String.valueOf(orderId)});
        db.close();
        return rowsAffected > 0;
    }

    // Xóa đơn hàng
    public boolean deleteOrder(int orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_ORDERS, "order_id = ?", new String[]{String.valueOf(orderId)});
        db.close();
        return rowsAffected > 0;
    }

    // Lấy đơn hàng theo adminId (người tạo sản phẩm)
    public List<Order> getOrdersByAdminId(int adminId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT o.* FROM " + TABLE_ORDERS + " o " +
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
        db.close();
        return orders;
    }
}