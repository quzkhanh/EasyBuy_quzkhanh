package com.example.easybuy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.easybuy.Model.Order;
import com.example.easybuy.Model.OrderDetail;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private static final String DATABASE_NAME = "EasyBuy.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_ORDER_DETAILS = "order_details";

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (order_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, total_price REAL, status TEXT, order_date TEXT)");
            db.execSQL("CREATE TABLE " + TABLE_ORDER_DETAILS + " (detail_id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER, product_id INTEGER, quantity INTEGER, price REAL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAILS);
            onCreate(db);
        }
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertOrder(Order order) {
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("total_price", order.getTotalPrice());
        values.put("status", order.getStatus());
        values.put("order_date", order.getOrderDate());
        return database.insert(TABLE_ORDERS, null, values);
    }

    public void insertOrderDetail(OrderDetail orderDetail) {
        ContentValues values = new ContentValues();
        values.put("order_id", orderDetail.getOrderId());
        values.put("product_id", orderDetail.getProductId());
        values.put("quantity", orderDetail.getQuantity());
        values.put("price", orderDetail.getPrice());
        database.insert(TABLE_ORDER_DETAILS, null, values);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_ORDERS, null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setOrderId(cursor.getInt(0));
                order.setUserId(cursor.getInt(1));
                order.setTotalPrice(cursor.getDouble(2));
                order.setStatus(cursor.getString(3));
                order.setOrderDate(cursor.getString(4));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_ORDER_DETAILS + " WHERE order_id = ?", new String[]{String.valueOf(orderId)});
        if (cursor.moveToFirst()) {
            do {
                OrderDetail detail = new OrderDetail();
                detail.setDetailId(cursor.getInt(0));
                detail.setOrderId(cursor.getInt(1));
                detail.setProductId(cursor.getInt(2));
                detail.setQuantity(cursor.getInt(3));
                detail.setPrice(cursor.getDouble(4));
                details.add(detail);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return details;
    }

    public void updateOrderStatus(int orderId, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        database.update(TABLE_ORDERS, values, "order_id = ?", new String[]{String.valueOf(orderId)});
    }
}