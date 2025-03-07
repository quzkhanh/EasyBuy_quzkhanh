package com.example.easybuy.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.Database.DatabaseHelper.DatabaseHelper;
import com.example.easybuy.Database.DatabaseHelper.OrderDatabaseHelper;
import com.example.easybuy.Model.Order;
import java.util.List;



public class OrderDAO {
    private final OrderDatabaseHelper orderDbHelper;

    public OrderDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.orderDbHelper = new OrderDatabaseHelper(dbHelper);
    }

    public long addOrder(Order order) {
        return orderDbHelper.addOrder(order);
    }

    public List<Order> getAllOrders() {
        return orderDbHelper.getAllOrders();
    }

    public List<Order> getOrdersByUserId(int userId) {
        return orderDbHelper.getOrdersByUserId(userId);
    }

    public boolean updateOrderStatus(int orderId, String status) {
        return orderDbHelper.updateOrderStatus(orderId, status);
    }

    public boolean deleteOrder(int orderId) {
        return orderDbHelper.deleteOrder(orderId);
    }

    public List<Order> getOrdersByAdminId(int adminId) {
        return orderDbHelper.getOrdersByAdminId(adminId);
    }
}