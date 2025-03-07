package com.example.easybuy.Activity.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Database.OrderAdminAdapter;
import com.example.easybuy.Database.OrderDAO;
import com.example.easybuy.Model.Order;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderFragment extends Fragment {

    private static final String TAG = "AdminOrderFragment";
    private RecyclerView recyclerViewOrders;
    private OrderAdminAdapter orderAdapter;
    private OrderDAO orderDAO;
    private int adminId;

    public AdminOrderFragment() {
        // Required empty public constructor
    }

    public static AdminOrderFragment newInstance(int adminId) {
        AdminOrderFragment fragment = new AdminOrderFragment();
        Bundle args = new Bundle();
        args.putInt("adminId", adminId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adminId = getArguments().getInt("adminId", -1);
        }
        try {
            orderDAO = new OrderDAO(requireContext());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing OrderDAO: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order, container, false);

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);

        if (orderDAO == null) {
            Toast.makeText(getContext(), "Không thể truy cập dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "OrderDAO is null");
            return view;
        }

        setupRecyclerView();


        return view;
    }

    private void setupRecyclerView() {
        try {
            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
            List<Order> orders = (adminId != -1) ? orderDAO.getOrdersByAdminId(adminId) : new ArrayList<>();
            orderAdapter = new OrderAdminAdapter(orders, order -> {
                // Không cần Toast nữa, đã xử lý trong dialog
            }, getContext()); // Truyền Context
            recyclerViewOrders.setAdapter(orderAdapter);

            if (orders.isEmpty()) {
                Toast.makeText(getContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi hiển thị danh sách đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }
}