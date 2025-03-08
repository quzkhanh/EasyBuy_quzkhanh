package com.example.easybuy.Activity.Admin;

import android.content.Context;

import com.example.easybuy.Database.DAO.OrderDAO;
import com.example.easybuy.Database.DAO.ProductDAO;
import com.example.easybuy.Model.Order;
import com.example.easybuy.Model.Product;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartManager {

    private Context context;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private int adminId;
    private int selectedYear;

    public ChartManager(Context context, int adminId, int selectedYear) {
        this.context = context;
        this.adminId = adminId;
        this.selectedYear = selectedYear;
        this.orderDAO = new OrderDAO(context);
        this.productDAO = new ProductDAO(context);
    }

    public void setupOrderChart(LineChart chart) {
        // Không cần nữa vì dùng PieChart
    }

    public void setupRevenueChart(LineChart chart) {
        // Không cần nữa vì dùng fragment riêng
    }

    public int getProductCount() {
        List<Product> products = productDAO.getProductsByAdmin(adminId);
        return products.size();
    }

    public int[] getOrderCountsByStatus() {
        List<Order> orders = orderDAO.getOrdersByAdminId(adminId);
        int[] counts = new int[5]; // Pending, Processing, Shipped, Delivered, Cancelled
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Order order : orders) {
            try {
                Date orderDate = dateFormat.parse(order.getOrderDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(orderDate);
                int orderYear = calendar.get(Calendar.YEAR);

                if (orderYear == selectedYear) {
                    String status = order.getStatus().toLowerCase();
                    switch (status) {
                        case "pending":
                            counts[0]++;
                            break;
                        case "processing":
                            counts[1]++;
                            break;
                        case "shipped":
                            counts[2]++;
                            break;
                        case "delivered":
                            counts[3]++;
                            break;
                        case "cancelled":
                            counts[4]++;
                            break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return counts;
    }

    public float[] getMonthlyRevenues() {
        List<Order> orders = orderDAO.getOrdersByAdminId(adminId);
        float[] revenues = new float[12]; // 12 tháng

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (Order order : orders) {
            try {
                Date orderDate = dateFormat.parse(order.getOrderDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(orderDate);
                int orderYear = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH); // 0-11

                if (orderYear == selectedYear && "Delivered".equalsIgnoreCase(order.getStatus())) {
                    revenues[month] += order.getTotalPrice();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return revenues;
    }
}