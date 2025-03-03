package com.example.easybuy.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.R;

public class AdminOrderFragment extends Fragment {

    private RecyclerView recyclerViewOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        // Thêm adapter để hiển thị danh sách đơn hàng (cần tạo OrderAdapter)

        Button btnViewOrderDetails = view.findViewById(R.id.btnViewOrderDetails);
        btnViewOrderDetails.setOnClickListener(v -> {
            // Chuyển đến màn hình chi tiết đơn hàng (cần tạo Activity hoặc Fragment mới)
        });
    }
}