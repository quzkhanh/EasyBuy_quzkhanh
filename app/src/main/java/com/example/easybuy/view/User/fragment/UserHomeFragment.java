package com.example.easybuy.view.User.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.R;
import com.example.easybuy.model.Order;
import com.example.easybuy.model.Product;
import com.example.easybuy.network.ApiClient;
import com.example.easybuy.network.ProductApi;
import com.example.easybuy.utils.SessionManager;
import com.example.easybuy.view.Adapter.ProductAdapter;
import com.example.easybuy.view.User.ProductDetailActivity;
import com.example.easybuy.database.dao.OrderDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private OrderDAO orderDAO;
    private ProductAdapter productAdapter;
    private TextView tvEmptyList;
    private EditText searchBar;
    private SessionManager sessionManager;
    private List<Product> allProducts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ View
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        searchBar = view.findViewById(R.id.searchBar);

        // Khởi tạo DAO & session
        orderDAO = new OrderDAO(requireContext());
        sessionManager = new SessionManager(requireContext());

        allProducts = new ArrayList<>();

        setupRecyclerView();
        loadProducts();        // Tải từ API
        setupSearchBar();      // Gắn bộ lọc

        return view;
    }

    private void setupRecyclerView() {
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        int currentUserId = sessionManager.getUserId();
        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), currentUserId, product -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getProductId());
            startActivity(intent);
        });

        productRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        ProductApi api = ApiClient.getClient().create(ProductApi.class);

        api.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    updateProductList(allProducts);
                } else {
                    Toast.makeText(getContext(), "Không tải được sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductList(List<Product> products) {
        if (products.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            productRecyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            productRecyclerView.setVisibility(View.VISIBLE);
            productAdapter.setProductList(products);
        }
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString());
            }
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(allProducts);
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
            for (Product product : allProducts) {
                if (product.getProductName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredList.add(product);
                }
            }
        }

        updateProductList(filteredList);
    }

    private void placeOrder(Product product) {
        if (!isUserLoggedIn()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        Order order = createOrder(product, currentDate, userId);
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
        order.setQuantity(1);
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
        loadProducts();
    }
}
