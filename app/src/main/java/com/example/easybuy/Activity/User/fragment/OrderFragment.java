package com.example.easybuy.Activity.User.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Database.DatabaseHelper;
import com.example.easybuy.Database.OrderAdapter;
import com.example.easybuy.Model.Order;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private TextView tvNoOrders;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        // Ánh xạ views
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());

        // Thiết lập RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext(), new ArrayList<>());
        recyclerViewOrders.setAdapter(orderAdapter);

        // Tải danh sách đơn hàng
        loadOrders();

        return view;
    }

    private void loadOrders() {
        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            // Giả sử bạn có phương thức getOrdersByUserId trong DatabaseHelper
            List<Order> orders = getOrdersByUserId(userId);

            if (orders.isEmpty()) {
                tvNoOrders.setVisibility(View.VISIBLE);
                recyclerViewOrders.setVisibility(View.GONE);
            } else {
                tvNoOrders.setVisibility(View.GONE);
                recyclerViewOrders.setVisibility(View.VISIBLE);
                orderAdapter.setOrderList(orders);
            }
        }
    }

    private List<Order> getOrdersByUserId(int userId) {
        // Thêm phương thức này vào DatabaseHelper
        return databaseHelper.getOrdersByUserId(userId);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders(); // Cập nhật lại danh sách khi quay lại fragment
    }
}