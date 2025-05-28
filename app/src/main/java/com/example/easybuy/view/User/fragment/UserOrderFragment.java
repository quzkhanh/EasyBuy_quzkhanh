package com.example.easybuy.view.User.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.database.dao.OrderDAO;
import com.example.easybuy.view.Adapter.OrderAdapter;
import com.example.easybuy.model.Order;
import com.example.easybuy.R;
import com.example.easybuy.utils.SessionManager;
import com.example.easybuy.view.User.UserMainActivity;

import java.util.ArrayList;
import java.util.List;

public class UserOrderFragment extends Fragment {
    private RecyclerView recyclerViewOrders;
    private LinearLayout emptyOrderLayout;
    private ImageView ivEmptyOrder;
    private TextView tvNoOrders;
    private OrderAdapter orderAdapter;
    private Button btnExploreNow;
    private OrderDAO orderDAO;
    private int userId;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_order, container, false);

        // Khởi tạo các view
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        emptyOrderLayout = view.findViewById(R.id.emptyOrderLayout);
        ivEmptyOrder = view.findViewById(R.id.ivEmptyOrder);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);
        btnExploreNow = view.findViewById(R.id.btnExploreNow);

        // Khởi tạo SessionManager và OrderDAO
        sessionManager = new SessionManager(requireContext());
        orderDAO = new OrderDAO(requireContext());

        // Lấy userId từ SessionManager
        userId = sessionManager.getUserId();
        // Xử lý nút Khám phá ngay
        btnExploreNow.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UserMainActivity.class));
        });
        setupRecyclerView();
        loadOrders();

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext(), new ArrayList<>());
        recyclerViewOrders.setAdapter(orderAdapter);

        // Thêm animation khi item xuất hiện
        recyclerViewOrders.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                getContext(), R.anim.layout_animation_fall_down));

        // Gắn Swipe-to-Delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new OrderAdapter.SwipeToDeleteCallback(orderAdapter, requireContext())
        );
        itemTouchHelper.attachToRecyclerView(recyclerViewOrders);
    }

    private void loadOrders() {
        if (userId != -1) {
            List<Order> orders = orderDAO.getOrdersByUserId(userId);

            if (orders.isEmpty()) {
                emptyOrderLayout.setVisibility(View.VISIBLE);
                recyclerViewOrders.setVisibility(View.GONE);
                tvNoOrders.setText("Oops! Bạn chưa có đơn hàng nào");
            } else {
                emptyOrderLayout.setVisibility(View.GONE);
                recyclerViewOrders.setVisibility(View.VISIBLE);
                orderAdapter.setOrderList(orders);
                recyclerViewOrders.scheduleLayoutAnimation(); // Chạy animation khi load
            }
        } else {
            emptyOrderLayout.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
            tvNoOrders.setText("Đăng nhập để xem đơn hàng!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders(); // Refresh danh sách khi quay lại
    }
}