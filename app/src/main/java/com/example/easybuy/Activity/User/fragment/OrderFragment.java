package com.example.easybuy.Activity.User.fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderCancelListener {
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private TextView tvNoOrders;
    private OrderDAO orderDAO;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        // Ánh xạ views
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);

        // Khởi tạo OrderDAO
        orderDAO = new OrderDAO(requireContext());

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Tải danh sách đơn hàng
        loadOrders();

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext(), new ArrayList<>(), this);
        recyclerViewOrders.setAdapter(orderAdapter);

        // Thêm chức năng trượt để hủy đơn
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
            // Xử lý trường hợp không có userId
            tvNoOrders.setText("Vui lòng đăng nhập để xem đơn hàng");
            tvNoOrders.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOrderCancel(Order order) {
        // Hiển thị dialog xác nhận hủy đơn
        new AlertDialog.Builder(requireContext())
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + order.getOrderId() + "?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    // Thực hiện hủy đơn hàng
                    boolean isDeleted = orderDAO.deleteOrder(order.getOrderId());
                    if (isDeleted) {
                        Toast.makeText(requireContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                        loadOrders(); // Tải lại danh sách đơn hàng
                    } else {
                        Toast.makeText(requireContext(), "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Quay lại", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders(); // Cập nhật lại danh sách khi quay lại fragment
    }
}