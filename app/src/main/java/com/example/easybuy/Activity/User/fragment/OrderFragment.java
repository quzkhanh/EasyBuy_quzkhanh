package com.example.easybuy.Activity.User.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Database.OrderDAO;
import com.example.easybuy.Database.OrderAdapter;
import com.example.easybuy.Model.Order;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private TextView tvNoOrders;
    private OrderDAO orderDAO;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);

        orderDAO = new OrderDAO(requireContext());

        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        setupRecyclerView();
        loadOrders();

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext(), new ArrayList<Order>());
        recyclerViewOrders.setAdapter(orderAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new OrderAdapter.SwipeToDeleteCallback(orderAdapter, requireContext())
        );
        itemTouchHelper.attachToRecyclerView(recyclerViewOrders);
    }

    private void loadOrders() {
        if (userId != -1) {
            List<Order> orders = orderDAO.getOrdersByUserId(userId);

            if (orders.isEmpty()) {
                tvNoOrders.setVisibility(View.VISIBLE);
                recyclerViewOrders.setVisibility(View.GONE);
            } else {
                tvNoOrders.setVisibility(View.GONE);
                recyclerViewOrders.setVisibility(View.VISIBLE);
                orderAdapter.setOrderList(orders);
            }
        } else {
            tvNoOrders.setText("Vui lòng đăng nhập để xem đơn hàng");
            tvNoOrders.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }
}