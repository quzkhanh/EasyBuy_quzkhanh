package com.example.easybuy.Activity.User.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Activity.User.ProductDetailActivity;
import com.example.easybuy.Database.OrderDAO;
import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Database.ProductAdapter;
import com.example.easybuy.Model.Order;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;
import com.example.easybuy.Utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserHomeFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private ProductAdapter productAdapter;
    private TextView tvEmptyList;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo các thành phần
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);

        // Khởi tạo DAO
        productDAO = new ProductDAO(requireContext());
        orderDAO = new OrderDAO(requireContext());

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(requireContext());

        // Cấu hình RecyclerView với GridLayoutManager (2 cột)
        setupRecyclerView();

        // Tải danh sách sản phẩm
        loadProducts();

        return view;
    }

    private void setupRecyclerView() {
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), product -> {
            // Chuyển sang ProductDetailActivity
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getProductId());
            startActivity(intent);
        });
        productRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();

        if (products.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            productRecyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            productRecyclerView.setVisibility(View.VISIBLE);
            productAdapter.setProductList(products);
        }
    }

    private void placeOrder(Product product) {
        // Kiểm tra đăng nhập
        if (!isUserLoggedIn()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy userId từ SessionManager
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thời gian hiện tại
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        // Tạo đơn hàng mới
        Order order = createOrder(product, currentDate, userId);

        // Lưu đơn hàng vào database
        long orderId = orderDAO.addOrder(order);

        if (orderId != -1) {
            Toast.makeText(getContext(), "Đặt hàng thành công: " + product.getProductName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show();
        }
    }

    private Order createOrder(Product product, String currentDate, int userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(product.getProductId());
        order.setQuantity(1); // Mặc định 1 sản phẩm
        order.setTotalPrice(product.getPrice());
        order.setStatus("Pending");
        order.setOrderDate(currentDate);
        return order;
    }

    private boolean isUserLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts(); // Cập nhật khi quay lại
    }
}