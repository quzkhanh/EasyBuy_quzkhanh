package com.example.easybuy.Activity.User.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.easybuy.Database.DatabaseHelper;
import com.example.easybuy.Database.ProductAdapter;
import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Model.Order;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductDAO productDAO;
    private DatabaseHelper databaseHelper; // Thêm DatabaseHelper để xử lý Order
    private ProductAdapter productAdapter;
    private TextView tvEmptyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo các thành phần
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        productDAO = new ProductDAO(requireContext());
        databaseHelper = new DatabaseHelper(requireContext());

        // Cấu hình RecyclerView với GridLayoutManager (2 cột)
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), product -> {
            // Chuyển sang ProductDetailActivity
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getProductId());
            startActivity(intent);
        });
        productRecyclerView.setAdapter(productAdapter);

        // Tải danh sách sản phẩm
        loadProducts();

        return view;
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();
        productAdapter.setProductList(products);
        tvEmptyList.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        productRecyclerView.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void placeOrder(Product product) {
        // Lấy userId từ SharedPreferences (giả định đã lưu khi đăng nhập)
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1); // -1 nếu chưa đăng nhập

        if (userId == -1) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thời gian hiện tại
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(product.getPrice()); // Giả định mua 1 sản phẩm, tổng giá = giá sản phẩm
        order.setStatus("Pending"); // Trạng thái ban đầu
        order.setOrderDate(currentDate);

        // Lưu đơn hàng vào database
        long orderId = databaseHelper.addOrder(order);
        if (orderId != -1) {
            Toast.makeText(getContext(), "Đặt hàng thành công: " + product.getProductName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts(); // Cập nhật khi quay lại
    }
}